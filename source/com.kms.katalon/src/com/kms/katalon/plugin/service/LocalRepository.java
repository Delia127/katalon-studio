package com.kms.katalon.plugin.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.core.util.ApplicationRunningMode;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.custom.factory.CustomKeywordPluginFactory;
import com.kms.katalon.custom.keyword.CustomKeywordPlugin;
import com.kms.katalon.entity.util.ZipManager;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.plugin.models.KStoreApiKeyCredentials;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreCredentials;
import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.ResolutionException;
import com.kms.katalon.plugin.models.ResolutionItem;
import com.kms.katalon.plugin.util.PluginFactory;
import com.kms.katalon.plugin.util.PluginHelper;
import com.kms.katalon.plugin.util.PluginSettings;
import com.kms.katalon.tracking.service.Trackings;

public class LocalRepository {
    
    private static LocalRepository instance;
    
    private LocalRepository() {
    };
    
    public static LocalRepository getInstance() {
        if (instance == null) {
            instance = new LocalRepository();
        }
        return instance;
    }

    public List<ResolutionItem> resolvePlugins(List<KStorePlugin> plugins,
                                               KStoreCredentials credentials,
                                               SubMonitor monitor) throws ResolutionException {
        
        List<ResolutionItem> resolutionItems = new ArrayList<>();
        
        Map<Long, Exception> errors = new HashMap<>();
        
        List<KStorePlugin> downloads = collectDownloads(plugins);
        
        prepareInstallFolder();
        
        try {
            prepareDownloadFolder();
        } catch (IOException e) {
            throw new ResolutionException("Fail to init download folder", e);
        }
        
        SubMonitor downloadMonitor = monitor.split(50, SubMonitor.SUPPRESS_NONE);
        downloadMonitor.beginTask("Downloading plugins...", 100);
        
        int work = 0;
        for (KStorePlugin plugin : downloads) {
            try {
                downloadPlugin(plugin, credentials);
            } catch (Exception e) {
                errors.put(plugin.getId(), e);
            }
            int progress = Math.round((float) ++work * 100 / downloads.size());
            downloadMonitor.worked(progress);
        }
        
        downloadMonitor.done();
        
        SubMonitor extractMonitor = monitor.split(50, SubMonitor.SUPPRESS_NONE);
        extractMonitor.beginTask("Extracting plugins...", 100);
        
        work = 0;
        for (KStorePlugin plugin : downloads) {
            if (!errors.containsKey(plugin.getId())) {
                File downloadFileInfo = getPluginDownloadFileInfo(plugin);
                try {
                    extractPlugin(plugin, downloadFileInfo);
                } catch (Exception e) {
                    errors.put(plugin.getId(), e);
                }
            }
            int progress = Math.round((float) ++work * 100 / downloads.size());
            extractMonitor.worked(progress);
        }
        
        extractMonitor.done();
        
        for (KStorePlugin plugin : plugins) {
            ResolutionItem resolutionItem = new ResolutionItem();
            resolutionItem.setPlugin(plugin);
            if (!errors.containsKey(plugin.getId())) {
                plugin.setFile(findBinaryJar(plugin));
            } else {
                resolutionItem.setException(errors.get(plugin.getId()));
            }
            resolutionItems.add(resolutionItem);
        }
        
        return resolutionItems;
    }
    
    private List<KStorePlugin> collectDownloads(List<KStorePlugin> plugins) {
        return plugins.stream()
                .filter(p -> !isLocallyInstalled(p) && !p.isExpired())
                .collect(Collectors.toList());
    }
    
    private boolean isLocallyInstalled(KStorePlugin plugin) {
        File pluginJarFile = findBinaryJar(plugin);
        return pluginJarFile != null;
    }
    
    private File findBinaryJar(KStorePlugin plugin) {
        File pluginInstallFolder = getPluginInstallFolder(plugin);
        if (!pluginInstallFolder.exists()) {
            return null;
        }
        File jar = Arrays.stream(pluginInstallFolder.listFiles())
            .filter(f -> {
                String name = f.getName().toLowerCase();
                return name.endsWith(".jar") && !name.endsWith("-javadoc.jar") && !name.endsWith("-sources.jar");
            }).findAny().orElse(null);
        return jar;
    }
    
    private void downloadPlugin(KStorePlugin plugin, KStoreCredentials credentials) throws IOException, KStoreClientException {
        File downloadFile = getPluginDownloadFileInfo(plugin);
        downloadFile.createNewFile();
        
        KStoreRestClient restClient = new KStoreRestClient(credentials);
        restClient.downloadPlugin(plugin.getProduct().getId(), 
            downloadFile,
            plugin.getLatestCompatibleVersion().getNumber());
        
        trackDownloadPlugin(plugin, credentials);
    }
    
    private void extractPlugin(KStorePlugin plugin, File downloadFile) throws Exception {
        File pluginInstallFolder = getPluginInstallFolder(plugin);
        pluginInstallFolder.mkdirs();
        ZipManager.unzip(downloadFile, pluginInstallFolder.getAbsolutePath());
    }
    
    private File getPluginDownloadFileInfo(KStorePlugin plugin) {
        String name = PluginHelper.idAndVersionKey(plugin);
        return new File(getRepoDownloadFolder(), name + ".zip");
    }
    
    private File getPluginInstallFolder(KStorePlugin plugin) {
        String pluginVersion = plugin.getLatestCompatibleVersion().getNumber();
        return new File(getRepoInstallFolder(), plugin.getId() + File.separator + pluginVersion);
    }
    
    private void prepareInstallFolder() {
        File installFolder = getRepoInstallFolder();
        installFolder.mkdirs();
    }
    
    private void prepareDownloadFolder() throws IOException {
        File downloadFolder = getRepoDownloadFolder();
        downloadFolder.mkdirs();
        FileUtils.cleanDirectory(downloadFolder);
    }
    
    private File getRepoInstallFolder() {
        return new File(PluginSettings.getPluginRepoDir(), "install");
    }
    
    private File getRepoDownloadFolder() {
        return new File(PluginSettings.getPluginRepoDir(), "download");
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
}
