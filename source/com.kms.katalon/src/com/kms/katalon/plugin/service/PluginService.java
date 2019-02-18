package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

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
                String pluginPath = getPluginLocation(plugin);
                if (!StringUtils.isBlank(pluginPath)) {
                    platformUninstall(pluginPath);
                }
    
                uninstallWork++;
                markWork(uninstallWork, totalUninstallWork, uninstallMonitor);
            }
    
            uninstallMonitor.done();
    
            SubMonitor installPluginMonitor = subMonitor.split(50, SubMonitor.SUPPRESS_NONE);
            installPluginMonitor.beginTask("Installing plugins...", 100);
    
            cleanUpDownloadDir();
    
            int totalInstallWork = latestPlugins.size();
            int installWork = 0;
            Set<String> installedBundleNames = new HashSet<>();
            for (KStorePlugin plugin : latestPlugins) {
                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
                if (plugin.isExpired()) {
                    ResultItem item = new ResultItem();
                    item.setPlugin(plugin);
                    results.add(item);
                    continue;
                }
                
                String pluginPath = getPluginLocation(plugin);
                if (!isPluginDownloaded(plugin)) {
                    File download = downloadAndExtractPlugin(plugin, credentials);
                    if (download != null) {
                        pluginPath = download.getAbsolutePath();
                        savePluginLocation(plugin, pluginPath);
                    }
                }
                
                String pluginBundleName = getPluginBundleName(pluginPath);
                if (!StringUtils.isBlank(pluginBundleName) && installedBundleNames.contains(pluginBundleName)) {
                    continue;
                }
                platformInstall(pluginPath);
                ResultItem item = new ResultItem();
                item.setPlugin(plugin);
                item.markPluginInstalled(true);
                if (VersionUtil.isNewer(plugin.getLatestVersion().getNumber(),
                    plugin.getCurrentVersion().getNumber())) {
                    item.setNewVersionAvailable(true);
                } else {
                    item.setNewVersionAvailable(false);
                }
                installedBundleNames.add(pluginBundleName);
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
        	if(StringUtils.containsIgnoreCase(e.getMessage(), EXCEPTION_UNAUTHORIZED_SINGAL)){
                throw new ReloadPluginsException("Unexpected error occurs during executing reload plugins due to invalid API Key", e);
        	}
        	throw new ReloadPluginsException("Unexpected error occurs during executing reload plugins", e);
        }
    }

    private void cleanUpDownloadDir() throws IOException {
        File downloadDir = getRepoDownloadDir();
        downloadDir.mkdirs();
        FileUtils.cleanDirectory(downloadDir);
    }

    private List<KStorePlugin> fetchLatestPlugins(KStoreCredentials credentials) throws KStoreClientException {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        List<KStorePlugin> latestPlugins = restClient.getLatestPlugins();
        return latestPlugins;
    }

    private List<KStorePlugin> getUninstalledPlugins(List<KStorePlugin> localPlugins,
            List<KStorePlugin> latestPlugins) {
        Map<Long, KStorePlugin> updatedPluginLookup = toMap(latestPlugins);
        List<KStorePlugin> uninstalledPlugins = new ArrayList<>();
        for (KStorePlugin plugin : localPlugins) {
            if (!updatedPluginLookup.containsKey(plugin.getId())) {
                uninstalledPlugins.add(plugin);
                continue;
            }
            KStorePlugin latestPluginInfo = updatedPluginLookup.get(plugin.getId());
            if (!latestPluginInfo.getCurrentVersion().getNumber().equals(
                    plugin.getCurrentVersion().getNumber())) {
                uninstalledPlugins.add(plugin);
                continue;
            }
            if (latestPluginInfo.isExpired()) {
                uninstalledPlugins.add(plugin);
            }
        }
        return uninstalledPlugins;
    }

    private Map<Long, KStorePlugin> toMap(List<KStorePlugin> plugins) {
        Map<Long, KStorePlugin> pluginMap = plugins.stream()
                .collect(Collectors.toMap(KStorePlugin::getId, Function.identity()));
        return pluginMap;
    }

    private String getPluginBundleName(String pluginPath) {
        try {
            File pluginFile = new File(pluginPath);
            JarFile jar = new JarFile(pluginFile);
            Manifest manifest = jar.getManifest();
            String bundleSymbolicNameAttribute = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
            String bundleSymbolicName = StringUtils.split(bundleSymbolicNameAttribute, ";")[0];
            jar.close();
            return bundleSymbolicName;
        } catch (Exception e) {
            return null;
        }
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

    private boolean isPluginDownloaded(KStorePlugin plugin) throws IOException {
        String pluginLocation = getPluginLocation(plugin);
        return !StringUtils.isBlank(pluginLocation) && new File(pluginLocation).exists();
    }

    private String getPluginLocation(KStorePlugin plugin) throws IOException {
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

        File downloadDir = getRepoDownloadDir();
        downloadDir.mkdirs();
        FileUtils.cleanDirectory(downloadDir);

        File downloadFile = getPluginDownloadFileInfo(plugin);
        downloadFile.createNewFile();

        KStoreRestClient restClient = getRestClient(credentials);
        restClient.downloadPlugin(plugin.getProduct().getId(), downloadFile);

        File pluginInstallDir = getPluginInstallDir(plugin);
        pluginInstallDir.mkdirs();
        ZipManager.unzip(downloadFile, pluginInstallDir.getAbsolutePath());

        File jar = Arrays.stream(pluginInstallDir.listFiles()).filter(file -> {
            String name = file.getName().toLowerCase();
            return name.endsWith(".jar") && !name.endsWith("-javadoc.jar") && !name.endsWith("-sources.jar");
        }).findAny().orElse(null);
        return jar;
    }

    private KStoreRestClient getRestClient(KStoreCredentials credentials) {
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        return restClient;
    }

    private File getPluginInstallDir(KStorePlugin plugin) {
        return new File(getRepoInstallDir(), 
                plugin.getId() +
                File.separator +
                plugin.getCurrentVersion().getNumber());
    }
    
    private File getPluginDownloadFileInfo(KStorePlugin plugin) {
        String fileName = PluginHelper.idAndVersionKey(plugin) + ".zip";
        return new File(getRepoDownloadDir(), fileName);
    }

    private File getRepoDownloadDir() {
        return new File(getPluginRepoDir(), "download");
    }

    private File getRepoInstallDir() {
        return new File(getPluginRepoDir(), "install");
    }

    private File getPluginRepoDir() {
        return new File(GlobalStringConstants.APP_USER_DIR_LOCATION, "plugin");
    }
}
