package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.katalon.platform.internal.api.PluginInstaller;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.util.ZipManager;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.KStoreApiKeyCredentials;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreClientExceptionWithInfo;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.KStoreProductType;
import com.kms.katalon.plugin.models.ReloadPluginsException;
import com.kms.katalon.plugin.models.ResultItem;
import com.kms.katalon.plugin.store.PluginPreferenceStore;
import com.kms.katalon.plugin.util.PluginFactory;
import com.kms.katalon.plugin.util.PluginHelper;
import com.kms.katalon.plugin.util.PluginSettings;
import com.kms.katalon.tracking.service.Trackings;

@SuppressWarnings("restriction")
public class PluginService {

    private static final String EXCEPTION_UNAUTHORIZED_SINGAL = "Unauthorized";

	private static PluginService instance;

    private IEventBroker eventBroker;

    private PluginPreferenceStore pluginPrefStore;

    private PluginService() {
        eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        pluginPrefStore = new PluginPreferenceStore();
    }

    public static PluginService getInstance() {
        if (instance == null) {
            instance = new PluginService();
        }
        return instance;
    }

    public List<ResultItem> reloadPlugins(KStoreCredentials credentials, IProgressMonitor monitor)
            throws ReloadPluginsException, InterruptedException {
        CustomKeywordPluginFactory.getInstance().clearPluginInStore();
        try {
            List<ResultItem> results = new ArrayList<>();

            SubMonitor subMonitor = SubMonitor.convert(monitor);
            subMonitor.beginTask("", 100);
    
            SubMonitor fetchDataMonitor = subMonitor.split(30, SubMonitor.SUPPRESS_NONE);
            fetchDataMonitor.beginTask("Fetching latest plugin info from Katalon Store...", 100);
    
            List<KStorePlugin> latestPlugins = fetchLatestPlugins(credentials);
    
            fetchDataMonitor.done();
    
            SubMonitor uninstallMonitor = subMonitor.split(20, SubMonitor.SUPPRESS_NONE);
            uninstallMonitor.beginTask("Uninstalling plugins...", 100);
    
            List<KStorePlugin> localPlugins = PluginFactory.getInstance().getPlugins();
    
            int totalUninstallWork = localPlugins.size();
            int uninstallWork = 0;
            for (KStorePlugin plugin : localPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                String pluginPath = plugin.getLocation();
                if (!StringUtils.isBlank(pluginPath)) {
                    platformUninstall(pluginPath);
                }
    
                uninstallWork++;
                markWork(uninstallWork, totalUninstallWork, uninstallMonitor);
            }
    
            uninstallMonitor.done();
    
            SubMonitor installPluginMonitor = subMonitor.split(40, SubMonitor.SUPPRESS_NONE);
            installPluginMonitor.beginTask("Installing plugins...", 100);
    
            cleanUpDownloadDir();
    
            int totalInstallWork = latestPlugins.size();
            int installWork = 0;

            CustomKeywordPluginFactory.getInstance().clearPluginInStore();
            PluginFactory.getInstance().clear();
            for (KStorePlugin plugin : latestPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }

                if (plugin.isExpired()) {
                    ResultItem item = new ResultItem();
                    item.setPlugin(plugin);
                    results.add(item);
                    LogService.getInstance().logInfo(String.format("Expired plugin: %d.", plugin.getId()));
                    continue;
                }
                
                if (plugin.getLatestCompatibleVersion() == null) {
                    ResultItem item = new ResultItem();
                    item.setPlugin(plugin);
                    results.add(item);
                    LogService.getInstance().logInfo(String.format("Plugin with latest compatible version: %d.", plugin.getId()));
                    continue;
                }
                
                String pluginPath = getPluginLocation(plugin);
                if (!isLocallyInstalled(plugin)) {
                    File download = downloadAndExtractPlugin(plugin, credentials);
                    if (download != null) {
                        pluginPath = download.getAbsolutePath();
                    }
                }
                plugin.setLocation(pluginPath);
                
                LogService.getInstance().logInfo(String.format("Plugin ID: %d. Plugin location: %s.",
                    plugin.getId(), pluginPath));
                
                try {
                    if (isCustomKeywordPlugin(plugin)) {
                        String location = getPluginLocation(plugin);
                        CustomKeywordPlugin customKeywordPlugin = new CustomKeywordPlugin();
                        customKeywordPlugin.setId(Long.toString(plugin.getId()));
                        File pluginFile = new File(location);
                        customKeywordPlugin.setPluginFile(pluginFile);

                        CustomKeywordPluginFactory.getInstance().addPluginFile(pluginFile, customKeywordPlugin);
                    } else {
                        platformInstall(pluginPath);
                    }
                    ResultItem item = new ResultItem();
                    item.setPlugin(plugin);
                    item.markPluginInstalled(true);
                    results.add(item);
                    PluginFactory.getInstance().addPlugin(plugin);
                } catch (BundleException e) {
                    LogService.getInstance().logError(e);
                    File pluginRepoDir = PluginSettings.getPluginRepoDir();
                    if (pluginRepoDir.exists()) {
                        pluginRepoDir.delete();
                    }
                    throw e;
                }

                installWork++;
                markWork(installWork, totalInstallWork, installPluginMonitor);
            }
    
            installPluginMonitor.done();
            
            SubMonitor refreshClasspathMonitor = subMonitor.split(10, SubMonitor.SUPPRESS_NONE);
            
            ProjectController projectController = ProjectController.getInstance();
            ProjectEntity currentProject = projectController.getCurrentProject();
            if (currentProject != null) {
                GroovyUtil.initGroovyProjectClassPath(currentProject,
                        projectController.getCustomKeywordPlugins(currentProject), false, refreshClasspathMonitor);
                KeywordController.getInstance().parseAllCustomKeywords(currentProject, null);
                if (ApplicationRunningMode.get() == RunningMode.GUI) {
                    eventBroker.post(EventConstants.KEYWORD_BROWSER_REFRESH, null);
                }
            }

            monitor.done();
    
            return results;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            if (StringUtils.containsIgnoreCase(e.getMessage(), EXCEPTION_UNAUTHORIZED_SINGAL)) {
                throw new ReloadPluginsException(
                        "Unexpected error occurs during executing reload plugins due to invalid API Key", e);
            }
            if (e instanceof KStoreClientExceptionWithInfo) {
                KStoreClientExceptionWithInfo castedE = (KStoreClientExceptionWithInfo) e;
                throw new ReloadPluginsException(
                        "Unexpected error occurs during executing reload plugins under account: " + castedE.getInfoMessage(), e);

            }
            throw new ReloadPluginsException("Unexpected error occurs during executing reload plugins", e);
        }
    }

    private void cleanUpDownloadDir() throws IOException {
        File downloadDir = getRepoDownloadDir();
        downloadDir.mkdirs();
        FileUtils.cleanDirectory(downloadDir);
    }

    private List<KStorePlugin> fetchLatestPlugins(KStoreCredentials credentials) throws KStoreClientExceptionWithInfo {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        String appVersion = VersionUtil.getCurrentVersion().getVersion();
        List<KStorePlugin> latestPlugins = restClient.getLatestPlugins(appVersion);
        latestPlugins.stream().forEach(p -> logPluginInfo(p));
        return latestPlugins;
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
        } catch (Exception ignored) {}
    }
    
    private Bundle platformInstall(String pluginPath) throws BundleException {
        BundleContext bundleContext = Platform.getBundle("com.katalon.platform").getBundleContext();
        String bundlePath = new File(pluginPath).toURI().toString();
        Bundle existingBundle = bundleContext.getBundle(bundlePath);
        if (existingBundle == null) {
            Bundle bundle = getPluginInstaller().installPlugin(bundleContext, bundlePath);
            if (bundle != null
                    && bundle.getSymbolicName().equals(IdConstants.JIRA_PLUGIN_ID)
                        && ApplicationRunningMode.get() != RunningMode.CONSOLE) {
                eventBroker.post(EventConstants.JIRA_PLUGIN_INSTALLED, null);
            }
            return bundle;
        } else {
            return existingBundle;
        }
    }
    
    private boolean existBundleWithSameSymbolicName(Bundle bundle) {
        BundleContext bundleContext = Platform.getBundle("com.katalon.platform").getBundleContext();
        Bundle[] bundles = bundleContext.getBundles();
        int count = 0;
        for (Bundle installedBundle : bundles) {
            if (StringUtils.equalsIgnoreCase(installedBundle.getSymbolicName(), bundle.getSymbolicName())) {
                count++;
                if (count >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private void platformUninstall(String pluginPath) throws BundleException {
        BundleContext bundleContext = InternalPlatform.getDefault().getBundleContext();
        String bundlePath = new File(pluginPath).toURI().toString();
        Bundle existingBundle = bundleContext.getBundle(bundlePath);
        if (existingBundle != null) {
            Bundle bundle = getPluginInstaller().uninstallPlugin(bundleContext, bundlePath);
            if (bundle != null
                    && bundle.getSymbolicName().equals(IdConstants.JIRA_PLUGIN_ID)
                        && ApplicationRunningMode.get() != RunningMode.CONSOLE) {
                eventBroker.post(EventConstants.JIRA_PLUGIN_UNINSTALLED, null);
            }
        }
    }
    
    private PluginInstaller getPluginInstaller() {
        BundleContext context = InternalPlatform.getDefault().getBundleContext();
        PluginInstaller pluginInstaller = context.getService(context.getServiceReference(PluginInstaller.class));
        return pluginInstaller;
    }
    
    private boolean isLocallyInstalled(KStorePlugin plugin) {
        File pluginFile = getPluginFile(plugin);
        if (pluginFile == null) {
            return false;
        }
        return pluginFile.exists();
    }

    private String getPluginLocation(KStorePlugin plugin) throws IOException {
        File pluginFile = getPluginFile(plugin);
        if (pluginFile == null) {
            return null;
        }
        return pluginFile.getAbsolutePath();
    }
    
    private File getPluginFile(KStorePlugin plugin) {
        File pluginInstallDir = getPluginInstallDir(plugin);
        if (!pluginInstallDir.exists()) {
            return null;
        }
        
        File jar = Arrays.stream(pluginInstallDir.listFiles()).filter(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".jar") && !name.endsWith("-javadoc.jar") && !name.endsWith("-sources.jar");
        }).findAny().orElse(null);
        return jar;
    }
    
    private boolean isCustomKeywordPlugin(KStorePlugin plugin) {
        return plugin.getProduct().getProductType().getName().equalsIgnoreCase(KStoreProductType.CUSTOM_KEYWORD);
    }

    private void markWork(int work, int totalWork, SubMonitor monitor) {
        int subwork = Math.round((float) work * 100 / totalWork);
        monitor.worked(subwork);
    }

    private File downloadAndExtractPlugin(KStorePlugin plugin, KStoreCredentials credentials) throws Exception {

        LogService.getInstance().logInfo("Downloaded plugin ID: " + plugin.getId());
        trackDownloadPlugin(plugin, credentials);
        
        File downloadDir = getRepoDownloadDir();
        downloadDir.mkdirs();
        FileUtils.cleanDirectory(downloadDir);

        File downloadFile = getPluginDownloadFileInfo(plugin);
        downloadFile.createNewFile();

        KStoreRestClient restClient = getRestClient(credentials);
        restClient.downloadPlugin(plugin.getProduct().getId(), downloadFile,
                plugin.getLatestCompatibleVersion().getNumber());

        File pluginInstallDir = getPluginInstallDir(plugin);
        pluginInstallDir.mkdirs();
        ZipManager.unzip(downloadFile, pluginInstallDir.getAbsolutePath());

        File jar = Arrays.stream(pluginInstallDir.listFiles()).filter(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".jar") && !name.endsWith("-javadoc.jar") && !name.endsWith("-sources.jar");
        }).findAny().orElse(null);
        return jar;
    }
    
    private void trackDownloadPlugin(KStorePlugin plugin, KStoreCredentials credentials) {
        String apiKey = credentials instanceof KStoreApiKeyCredentials
            ? ((KStoreApiKeyCredentials) credentials).getApiKey() 
            : StringUtils.EMPTY;
        Trackings.trackDownloadPlugin(
            apiKey,
            plugin.getProduct().getId(),
            plugin.getProduct().getName(),
            plugin.getLatestCompatibleVersion().getNumber(),
            ApplicationRunningMode.get());
    }

    private KStoreRestClient getRestClient(KStoreCredentials credentials) {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        return restClient;
    }

    private File getPluginInstallDir(KStorePlugin plugin) {
        return new File(getRepoInstallDir(), 
                plugin.getId() +
                File.separator +
                plugin.getLatestCompatibleVersion().getNumber());
    }
    
    private File getPluginDownloadFileInfo(KStorePlugin plugin) {
        String fileName = PluginHelper.idAndVersionKey(plugin) + ".zip";
        return new File(getRepoDownloadDir(), fileName);
    }

    private File getRepoDownloadDir() {
        return new File(PluginSettings.getPluginRepoDir(), "download");
    }

    private File getRepoInstallDir() {
        return new File(PluginSettings.getPluginRepoDir(), "install");
    }
}
