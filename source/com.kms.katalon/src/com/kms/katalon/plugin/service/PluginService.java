package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.model.RunningMode;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.entity.util.ZipManager;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.ReloadPluginsException;
import com.kms.katalon.plugin.models.ResultItem;
import com.kms.katalon.plugin.store.PluginPreferenceStore;
import com.kms.katalon.plugin.util.PluginHelper;

@SuppressWarnings("restriction")
public class PluginService {

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
    
            List<KStorePlugin> localPlugins = pluginPrefStore.getInstalledPlugins();
            List<KStorePlugin> uninstalledPlugins = getUninstalledPlugins(localPlugins, latestPlugins);
    
            int totalUninstallWork = uninstalledPlugins.size();
            int uninstallWork = 0;
            for (KStorePlugin plugin : uninstalledPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                String pluginPath = getPluginFolderLocation(plugin);
                if (!StringUtils.isBlank(pluginPath)) {
                    platformUninstall(pluginPath);
                }
    
                ResultItem item = new ResultItem();
                item.setPlugin(plugin);
                item.markPluginInstalled(false);
                results.add(item);
    
                uninstallWork++;
                markWork(uninstallWork, totalUninstallWork, uninstallMonitor);
            }
    
            uninstallMonitor.done();
    
            SubMonitor installPluginMonitor = subMonitor.split(50, SubMonitor.SUPPRESS_NONE);
            installPluginMonitor.beginTask("Installing plugins...", 100);
    
            cleanUpDownloadDir();
    
            int totalInstallWork = latestPlugins.size();
            int installWork = 0;
            for (KStorePlugin plugin : latestPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                String pluginPath = getPluginFolderLocation(plugin);
                if (!isPluginDownloaded(plugin)) {
                    File download = downloadAndExtractPlugin(plugin, credentials);
                    if (download != null) {
                        pluginPath = download.getAbsolutePath();
                        savePluginLocation(plugin, pluginPath);
                    }
                }
                platformInstall(pluginPath);

                ResultItem item = new ResultItem();
                item.setPlugin(plugin);
                item.markPluginInstalled(true);
                results.add(item);
                
                installWork++;
                markWork(installWork, totalInstallWork, installPluginMonitor);
            }
    
            installPluginMonitor.done();
    
            pluginPrefStore.setInstalledPlugins(latestPlugins);
    
            monitor.done();
    
            return results;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new ReloadPluginsException("Unexpected error occurs during executing reload plugins.", e);
        }
    }

    private void cleanUpDownloadDir() throws IOException {
        File downloadDir = getPluginDownloadDir();
        downloadDir.mkdirs();
        FileUtils.cleanDirectory(downloadDir);
    }

    public boolean checkForPluginUpdates(KStoreCredentials credentials, IProgressMonitor monitor) throws KStoreClientException {
        List<KStorePlugin> localPlugins = pluginPrefStore.getInstalledPlugins();
        List<KStorePlugin> latestPlugins = fetchLatestPlugins(credentials);
        List<KStorePlugin> uninstalledPlugins = getUninstalledPlugins(localPlugins, latestPlugins);
        List<KStorePlugin> newInstalledPlugins = getNewInstalledPlugins(localPlugins, latestPlugins);
        return CollectionUtils.isNotEmpty(uninstalledPlugins) || CollectionUtils.isNotEmpty(newInstalledPlugins);
    }

    private List<KStorePlugin> fetchLatestPlugins(KStoreCredentials credentials) throws KStoreClientException {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        List<KStorePlugin> latestPlugins = restClient.getLatestPlugins();
        return latestPlugins;
    }

    private List<KStorePlugin> getUninstalledPlugins(List<KStorePlugin> localPlugins,
            List<KStorePlugin> updatedPlugins) {
        Map<Long, KStorePlugin> updatedPluginLookup = toMap(updatedPlugins);
        return localPlugins.stream().filter(p -> !updatedPluginLookup.containsKey(p.getId()))
                .collect(Collectors.toList());
    }

    private List<KStorePlugin> getNewInstalledPlugins(List<KStorePlugin> localPlugins,
            List<KStorePlugin> latestPlugins) {
        Map<Long, KStorePlugin> localPluginLookup = toMap(localPlugins);
        return latestPlugins.stream().filter(p -> !localPluginLookup.containsKey(p.getId()))
                .collect(Collectors.toList());
    }

    private Map<Long, KStorePlugin> toMap(List<KStorePlugin> plugins) {
        Map<Long, KStorePlugin> pluginMap = plugins.stream()
                .collect(Collectors.toMap(KStorePlugin::getId, Function.identity()));
        return pluginMap;
    }

    private void platformInstall(String pluginPath) throws BundleException {
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
        }
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

    private boolean isPluginDownloaded(KStorePlugin plugin) {
        String pluginLocation = getPluginFolderLocation(plugin);
        return !StringUtils.isBlank(pluginLocation) && new File(pluginLocation).exists();
    }

    private String getPluginFolderLocation(KStorePlugin plugin) {
        return pluginPrefStore.getPluginLocation(plugin);
    }

    private void savePluginLocation(KStorePlugin plugin, String path) throws IOException {
        pluginPrefStore.setPluginLocation(plugin, path);
    }

    private void markWork(int work, int totalWork, SubMonitor monitor) {
        int subwork = Math.round((float) work * 100 / totalWork);
        monitor.worked(subwork);
    }

    private File downloadAndExtractPlugin(KStorePlugin plugin, KStoreCredentials credentials) throws Exception {

        File downloadDir = getPluginDownloadDir();
        downloadDir.mkdirs();
        FileUtils.cleanDirectory(downloadDir);

        File downloadFile = getPluginDownloadFileInfo(plugin);
        downloadFile.createNewFile();

        KStoreRestClient restClient = getRestClient(credentials);
        restClient.downloadPlugin(plugin.getProduct().getId(), downloadFile);

        File installDir = getPluginInstallDir();
        installDir.mkdirs();
        File outputDir = new File(getPluginInstallDir(), PluginHelper.idAndVersionKey(plugin));
        outputDir.mkdirs();
        ZipManager.unzip(downloadFile, outputDir.getAbsolutePath());

        File jar = Arrays.stream(outputDir.listFiles()).filter(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".jar") && !name.endsWith("-javadoc.jar") && !name.endsWith("-sources.jar");
        }).findAny().orElse(null);
        return jar;
    }

    private KStoreRestClient getRestClient(KStoreCredentials credentials) {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        return restClient;
    }

    private File getPluginDownloadFileInfo(KStorePlugin plugin) throws IOException {
        String fileName = PluginHelper.idAndVersionKey(plugin) + ".zip";
        return new File(getPluginDownloadDir(), fileName);
    }

    private File getPluginDownloadDir() throws IOException {
        return new File(getPluginAppDir(), "download");
    }

    private File getPluginInstallDir() throws IOException {
        return new File(getPluginAppDir(), "install");
    }

    private File getPluginAppDir() throws IOException {
        return new File(GlobalStringConstants.APP_USER_DIR_LOCATION, "plugin");
    }
}
