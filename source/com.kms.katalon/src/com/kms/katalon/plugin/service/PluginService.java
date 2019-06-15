package com.kms.katalon.plugin.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.KStoreApiKeyCredentials;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreClientExceptionWithInfo;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.ReloadItem;
import com.kms.katalon.plugin.models.ReloadPluginsException;
import com.kms.katalon.plugin.models.ResolutionItem;
import com.kms.katalon.plugin.util.PlatformHelper;
import com.kms.katalon.plugin.util.PluginFactory;
import com.kms.katalon.plugin.util.PluginHelper;
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
    
            SubMonitor fetchDataMonitor = subMonitor.split(20, SubMonitor.SUPPRESS_NONE);
            fetchDataMonitor.beginTask("Fetching latest plugin info from Katalon Store...", 100);
    
            List<KStorePlugin> latestPlugins = fetchLatestPlugins(credentials);
    
            fetchDataMonitor.done();
    
            SubMonitor uninstallMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            uninstallMonitor.beginTask("Uninstalling plugins...", 100);
    
            List<KStorePlugin> localPlugins = PluginFactory.getInstance().getPlugins();
    
            int totalUninstallWork = localPlugins.size();
            int uninstallWork = 0;
            for (KStorePlugin plugin : localPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                PlatformHelper.uninstallPlugin(plugin);
                uninstallWork++;
                markWork(uninstallWork, totalUninstallWork, uninstallMonitor);
            }
    
            uninstallMonitor.done();
            
            SubMonitor resolveMonitor = subMonitor.split(40, SubMonitor.SUPPRESS_NONE);
            resolveMonitor.beginTask("Resolving plugins...", 100);
            
            List<ResolutionItem> resolutionItems = LocalRepository.getInstance().resolvePlugins(latestPlugins,
                    credentials, resolveMonitor);
            
            resolveMonitor.done();
    
            SubMonitor installPluginMonitor = subMonitor.split(20, SubMonitor.SUPPRESS_NONE);
            installPluginMonitor.beginTask("Installing plugins...", 100);
    
            int totalInstallWork = latestPlugins.size();
            int installWork = 0;

            CustomKeywordPluginFactory.getInstance().clearPluginInStore();
            PluginFactory.getInstance().clear();
            for (ResolutionItem resolutionItem : resolutionItems) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                
                KStorePlugin plugin = resolutionItem.getPlugin();
                ReloadItem reloadItem = new ReloadItem();
                reloadItem.setPlugin(plugin);
                results.add(reloadItem);
                
                if (resolutionItem.getException() != null) {
                    reloadItem.setException(resolutionItem.getException());
                    continue;
                }
                
                if (plugin.isExpired()) {
                    LogService.getInstance().logInfo(String.format("Expired plugin: %d.", plugin.getId()));
                    continue;
                }
                
                if (plugin.getLatestCompatibleVersion() == null) {
                    LogService.getInstance().logInfo(String.format("Plugin with latest compatible version: %d.", plugin.getId()));
                    continue;
                }
                
                
                LogService.getInstance().logInfo(String.format("Plugin ID: %d. Plugin location: %s.",
                    plugin.getId(), plugin.getFile().getAbsolutePath()));
                
                try {
                    if (PluginHelper.isCustomKeywordPlugin(plugin)) {
                        CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
                        customKeywordPlugin.setId(Long.toString(plugin.getId()));
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
                throw new ReloadPluginsException(
                        "Error occurs during executing reload plugins due to invalid API Key", e);
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
    private List<KStorePlugin> fetchLatestPlugins(KStoreCredentials credentials) throws KStoreClientException {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        String appVersion = VersionUtil.getCurrentVersion().getVersion();
        List<KStorePlugin> latestPlugins = null;
        try {
            latestPlugins = restClient.getLatestPlugins(appVersion);
        } catch (KStoreClientExceptionWithInfo e) {
            LoggerSingleton.logError(e);
        }
        latestPlugins.stream().forEach(p -> logPluginInfo(p));
        return latestPlugins;
    }
    
    public void logPluginInfo(KStorePlugin plugin) {
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
        } catch (Exception ignored) {}
    }
    
    private void refreshProjectClasspath(SubMonitor monitor) throws Exception {
        ProjectController projectController = ProjectController.getInstance();
        ProjectEntity currentProject = projectController.getCurrentProject();
        if (currentProject != null) {
            GroovyUtil.initGroovyProjectClassPath(currentProject,
                    projectController.getCustomKeywordPlugins(currentProject), false, monitor);
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
    
    private void trackInstallPlugins(List<KStorePlugin> plugins, KStoreCredentials credentials, RunningMode runningMode) {
        List<Long> installedPluginIds = PluginFactory.getInstance().getPlugins().stream()
                .map(p -> p.getProduct().getId()).collect(Collectors.toList());
        if (credentials instanceof KStoreApiKeyCredentials) {
            Trackings.trackInstallPlugins(installedPluginIds, ((KStoreApiKeyCredentials) credentials).getApiKey(),
                    runningMode);
        } else {
            Trackings.trackInstallPlugins(installedPluginIds, StringUtils.EMPTY, runningMode);
        }
    }
}
