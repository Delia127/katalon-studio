package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.application.KatalonApplication;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.KatalonPackage;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.constants.PluginOptions;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.TestOpsFeatureKey;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.KStoreApiKeyCredentials;
import com.kms.katalon.plugin.models.KStoreClientExceptionWithInfo;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreProduct;
import com.kms.katalon.plugin.models.OfflinePlugin;
import com.kms.katalon.plugin.models.Plugin;
import com.kms.katalon.plugin.models.ReloadItem;
import com.kms.katalon.plugin.models.ReloadPluginsException;
import com.kms.katalon.plugin.models.ResolutionItem;
import com.kms.katalon.plugin.util.KStoreCredentialsHelper;
import com.kms.katalon.plugin.util.PlatformHelper;
import com.kms.katalon.plugin.util.PluginFactory;
import com.kms.katalon.plugin.util.PluginSettings;
import com.kms.katalon.tracking.service.Trackings;

public class PluginService {
    private static final String EXCEPTION_UNAUTHORIZED_SINGAL = "Unauthorized";

    private static PluginService instance;

    private IEventBroker eventBroker;

    private PluginService() {
        eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
    }

    public static PluginService getInstance() {
        if (instance == null) {
            instance = new PluginService();
        }
        return instance;
    }

    public List<ReloadItem> reloadPlugins(KStoreCredentials credentials, IProgressMonitor monitor)
            throws ReloadPluginsException, InterruptedException {
        CustomKeywordPluginFactory.getInstance().clearPluginInStore();
        try {
            List<ReloadItem> results = new ArrayList<>();

            SubMonitor subMonitor = SubMonitor.convert(monitor);
            subMonitor.beginTask("", 100);

            SubMonitor getOnlinePluginsMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            
            List<KStorePlugin> onlinePlugins;
            if (shouldReloadPluginsOnline() && KStoreCredentialsHelper.isValidCredential(credentials)) {
                getOnlinePluginsMonitor.beginTask("Fetching latest plugins info from Katalon Store...", 100);
    
                onlinePlugins = getOnlinePlugins(credentials);
                
                getOnlinePluginsMonitor.done();
            } else {
                onlinePlugins = Collections.emptyList();
            }
            
            getOnlinePluginsMonitor.done();
            
            SubMonitor getOfflinePluginsMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            
            List<OfflinePlugin> offlinePlugins;
            if (shouldReloadPluginsOffline()) {
                getOfflinePluginsMonitor.beginTask("Getting offline plugins info...", 100);
    
                offlinePlugins = getOfflinePlugins(getOfflinePluginsMonitor);
    
                getOfflinePluginsMonitor.done();
            } else {
                offlinePlugins = Collections.emptyList();
            }
            
            getOfflinePluginsMonitor.done();

            SubMonitor uninstallMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            uninstallMonitor.beginTask("Uninstalling plugins...", 100);

            List<Plugin> installedPlugins = PluginFactory.getInstance().getPlugins();

            int totalUninstallWork = installedPlugins.size();
            int uninstallWork = 0;
            for (Plugin plugin : installedPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                PlatformHelper.uninstallPlugin(plugin);
                uninstallWork++;
                markWork(uninstallWork, totalUninstallWork, uninstallMonitor);
            }

            uninstallMonitor.done();

            SubMonitor resolveOnlinePluginMonitor = subMonitor.split(30, SubMonitor.SUPPRESS_NONE);
            resolveOnlinePluginMonitor.beginTask("Resolving online plugins...", 100);

            List<ResolutionItem> onlinePluginResolutionItems = PluginResolver.getInstance()
                    .resolveOnlinePlugins(onlinePlugins, credentials, resolveOnlinePluginMonitor);

            resolveOnlinePluginMonitor.done();

            SubMonitor resolveOfflinePluginMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            resolveOfflinePluginMonitor.beginTask("Resolving offline plugins...", 100);

            List<ResolutionItem> offlinePluginResolutionItems = PluginResolver.getInstance()
                    .resolveOfflinePlugins(offlinePlugins, resolveOfflinePluginMonitor);

            resolveOfflinePluginMonitor.done();

            List<ResolutionItem> resolutionItems = new ArrayList<>();
            resolutionItems.addAll(onlinePluginResolutionItems);
            resolutionItems.addAll(offlinePluginResolutionItems);

            SubMonitor installPluginMonitor = subMonitor.split(20, SubMonitor.SUPPRESS_NONE);
            installPluginMonitor.beginTask("Installing plugins...", 100);

            int totalInstallWork = onlinePlugins.size();
            int installWork = 0;

            CustomKeywordPluginFactory.getInstance().clearPluginInStore();
            PluginFactory.getInstance().clear();
            for (ResolutionItem resolutionItem : resolutionItems) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }

                Plugin plugin = resolutionItem.getPlugin();
                ReloadItem reloadItem = new ReloadItem();
                reloadItem.setPlugin(plugin);
                results.add(reloadItem);

                if (resolutionItem.getException() != null) {
                    reloadItem.setException(resolutionItem.getException());
                    continue;
                }

                if (plugin.isOnline()) {
                    KStorePlugin onlinePlugin = plugin.getOnlinePlugin();
                    if (onlinePlugin.isExpired()) { // offline plugin never
                                                    // expires
                        LogService.getInstance().logInfo(String.format("Expired plugin: %d.", onlinePlugin.getId()));
                        continue;
                    }

                    if (onlinePlugin.getLatestCompatibleVersion() == null) {
                        LogService.getInstance().logInfo(
                                String.format("Plugin with latest compatible version: %d.", onlinePlugin.getId()));
                        continue;
                    }
                }

                LogService.getInstance().logInfo(String.format("Plugin name: %s. Is online: %b. Plugin location: %s.",
                        plugin.getName(), plugin.isOnline(), plugin.getFile().getAbsolutePath()));

                try {
                    if (plugin.isCustomKeywordPlugin()) {
                        CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
                        customKeywordPlugin.setId(plugin.getFile().getAbsolutePath());
                        File pluginFile = plugin.getFile();
                        customKeywordPlugin.setPluginFile(pluginFile);
                        CustomKeywordPluginFactory.getInstance().addPluginFile(pluginFile, customKeywordPlugin);
                    } else {
                        PlatformHelper.installPlugin(plugin);
                    }
                    reloadItem.markPluginInstalled(true);
                    PluginFactory.getInstance().addPlugin(plugin);
                } catch (Exception e) {
                    LogService.getInstance().logError(e);
                    File pluginRepoDir = PluginSettings.getPluginRepoDir();
                    if (pluginRepoDir.exists()) {
                        pluginRepoDir.delete();
                    }
                    reloadItem.setException(e);
                }

                installWork++;
                markWork(installWork, totalInstallWork, installPluginMonitor);
            }
            
            installPluginMonitor.done();

            SubMonitor refreshClasspathMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            refreshClasspathMonitor.beginTask("Refreshing classpath...", 100);

            refreshProjectClasspath(refreshClasspathMonitor);

            refreshClasspathMonitor.done();

            trackInstallPlugins(PluginFactory.getInstance().getPlugins(), credentials, ApplicationRunningMode.get());

            monitor.done();

            return results;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            if (StringUtils.containsIgnoreCase(e.getMessage(), EXCEPTION_UNAUTHORIZED_SINGAL)) {
                throw new ReloadPluginsException("Error occurs during executing reload plugins due to invalid API Key",
                        e);
            }
            if (e instanceof KStoreClientExceptionWithInfo) {
                KStoreClientExceptionWithInfo castedE = (KStoreClientExceptionWithInfo) e;
                throw new ReloadPluginsException(
                        "Unexpected error occurs during executing reload plugins under account: "
                                + castedE.getInfoMessage(),
                        e);
            }
            throw new ReloadPluginsException("Unexpected error occurs during executing reload plugins", e);
        }
    }
    
    public boolean shouldReloadPluginsOnline() throws IOException {
        PluginOptions reloadOption = PluginSettings.getReloadPluginOption();
        return reloadOption == PluginOptions.ONLINE || reloadOption == PluginOptions.ONLINE_AND_OFFLINE;
    }

    public boolean shouldReloadPluginsOnlineOnly() throws IOException {
        PluginOptions reloadOption = PluginSettings.getReloadPluginOption();
        return reloadOption == PluginOptions.ONLINE;
    }
    
    public boolean shouldReloadPluginsOffline() throws IOException {
        PluginOptions reloadOption = PluginSettings.getReloadPluginOption();
        return reloadOption == PluginOptions.OFFLINE || reloadOption == PluginOptions.ONLINE_AND_OFFLINE;
    }

    private List<KStorePlugin> getOnlinePlugins(KStoreCredentials credentials) throws KStoreClientExceptionWithInfo {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        String appVersion = VersionUtil.getCurrentVersion().getVersion();
        KatalonPackage katalonPackage = KatalonApplication.getKatalonPackage();
        LicenseType licenseType = ActivationInfoCollector.getLicenseType();
        List<KStorePlugin> latestPlugins = null;
        try {
            latestPlugins = restClient.getLatestPlugins(appVersion, katalonPackage, licenseType);
        } catch (KStoreClientExceptionWithInfo e) {
            LoggerSingleton.logError(e);
        }
        latestPlugins.stream().forEach(p -> logPluginInfo(p));
        return latestPlugins;
    }

    private List<OfflinePlugin> getOfflinePlugins(IProgressMonitor progressMonitor) {
        boolean canUsePrivatePlugins = FeatureServiceConsumer.getServiceInstance()
                .canUse(TestOpsFeatureKey.PRIVATE_PLUGIN);

        SubMonitor monitor = SubMonitor.convert(progressMonitor);
        monitor.beginTask("", 100);

        List<OfflinePlugin> offlinePlugins = new ArrayList<>();

        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (project != null) {
            File pluginsFolder = new File(project.getFolderLocation(), "Plugins");
            if (pluginsFolder.exists()) {
                File[] files = pluginsFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && FilenameUtils.getExtension(file.getAbsolutePath()).equals("jar")) {
                            OfflinePlugin plugin = new OfflinePlugin();
                            plugin.setName(FilenameUtils.getName(file.getAbsolutePath()));
                            plugin.setFile(file);
                            plugin.setCustomKeywordPlugin(true);
                            offlinePlugins.add(plugin);
                        }
                    }
                }
            }
            monitor.worked(50);

            File platformPluginsFolder = new File(pluginsFolder, "platform");
            if (platformPluginsFolder.exists()) {
                File[] files = platformPluginsFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && FilenameUtils.getExtension(file.getAbsolutePath()).equals("jar")) {
                            OfflinePlugin plugin = new OfflinePlugin();
                            plugin.setName(FilenameUtils.getName(file.getAbsolutePath()));
                            plugin.setFile(file);
                            plugin.setCustomKeywordPlugin(false);
                            offlinePlugins.add(plugin);
                        }
                    }
                }
            }
            monitor.worked(50);
        }
        
        if (canUsePrivatePlugins) {
            return offlinePlugins;
        } else {
            //restric to use 1 custom keyword plugin only
            if (offlinePlugins.size() > 0 && offlinePlugins.get(0).isCustomKeywordPlugin()) {
                OfflinePlugin plugin = offlinePlugins.get(0);
                offlinePlugins = new ArrayList<>();
                offlinePlugins.add(plugin);
            } else {
                offlinePlugins = Collections.emptyList();
            }
            return offlinePlugins;
        }
    }

    private void logPluginInfo(KStorePlugin plugin) {
        try {
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("id", plugin.getId());
            infoMap.put("productId", plugin.getProduct().getId());
            infoMap.put("name", plugin.getProduct().getName());
            infoMap.put("expired", plugin.isExpired());
            if (ApplicationRunningMode.get() == RunningMode.GUI) {
                LoggerSingleton.logInfo("Plugin info: " + JsonUtil.toJson(infoMap));
            } else {
                LogUtil.printOutputLine("Plugin info: " + JsonUtil.toJson(infoMap));
            }
        } catch (Exception ignored) {
        }
    }

    public void logPluginProductInfo(KStoreProduct plugin) {
        try {
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put("id", plugin.getId());
            infoMap.put("productId", plugin.getId());
            infoMap.put("name", plugin.getName());
            if (ApplicationRunningMode.get() == RunningMode.GUI) {
                LoggerSingleton.logInfo("Plugin info: " + JsonUtil.toJson(infoMap));
            } else {
                LogUtil.printOutputLine("Plugin info: " + JsonUtil.toJson(infoMap));
            }
        } catch (Exception ignored) {}
    }


    private void refreshProjectClasspath(SubMonitor monitor) throws Exception {
        ProjectController projectController = ProjectController.getInstance();
        ProjectEntity currentProject = projectController.getCurrentProject();
        if (currentProject != null) {
            GroovyUtil.initGroovyProjectClassPath(currentProject,
                    projectController.getCustomKeywordPlugins(currentProject), false, monitor);
            projectController.updateProjectClassLoader(currentProject);
            KeywordController.getInstance().parseAllCustomKeywords(currentProject, null);
            if (ApplicationRunningMode.get() == RunningMode.GUI) {
                eventBroker.post(EventConstants.KEYWORD_BROWSER_REFRESH, null);
            }
        }
    }

    private void markWork(int work, int totalWork, SubMonitor monitor) {
        int subwork = Math.round((float) work * 100 / totalWork);
        monitor.worked(subwork);
    }

    private void trackInstallPlugins(List<Plugin> plugins, KStoreCredentials credentials, RunningMode runningMode) {
        // just track online plugins for now
        List<Long> installedPluginIds = plugins.stream().filter(p -> p.isOnline())
                .map(p -> p.getOnlinePlugin().getProduct().getId()).collect(Collectors.toList());
        if (credentials instanceof KStoreApiKeyCredentials) {
            Trackings.trackInstallPlugins(installedPluginIds, ((KStoreApiKeyCredentials) credentials).getApiKey(),
                    runningMode);
        } else {
            Trackings.trackInstallPlugins(installedPluginIds, StringUtils.EMPTY, runningMode);
        }
    }
}
