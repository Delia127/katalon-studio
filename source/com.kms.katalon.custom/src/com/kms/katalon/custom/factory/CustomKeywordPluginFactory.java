package com.kms.katalon.custom.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kms.katalon.custom.keyword.CustomKeywordPlugin;

public class CustomKeywordPluginFactory {
    
    public static final String DEV_PLUGIN_ID = "devPlugin";

    private static CustomKeywordPluginFactory instance;

    private Map<String, CustomKeywordPlugin> customKeywordPluginCollection = new HashMap<>();
    
    private CustomKeywordPlugin devPlugin;

    public static CustomKeywordPluginFactory getInstance() {
        if (instance == null) {
            instance = new CustomKeywordPluginFactory();
        }
        return instance;
    }

    
    public void addDevPluginFile(File file) {
        devPlugin = new CustomKeywordPlugin();
        devPlugin.setId(DEV_PLUGIN_ID);
        devPlugin.setPluginFile(file);
    }

    public void addPluginFile(File file, CustomKeywordPlugin plugin) {
        customKeywordPluginCollection.put(file.getAbsolutePath(), plugin);
    }

    public CustomKeywordPlugin getByPath(String path) {
        return customKeywordPluginCollection.get(path);
    }
    
    public List<CustomKeywordPlugin> getPlugins() {
        List<CustomKeywordPlugin> plugins = new ArrayList<>(customKeywordPluginCollection.entrySet()
                .stream()
                .filter(e -> e.getValue().getPluginFile() != null)
                .map(plugin -> plugin.getValue())
                .collect(Collectors.toList()));
        if (devPlugin != null) {
            plugins.add(devPlugin);
        }
        return plugins;
    }
    
    public List<File> getStoredPluginFiles() {
        List<File> pluginFiles = new ArrayList<>(customKeywordPluginCollection.entrySet()
                .stream()
                .filter(e -> e.getValue().getPluginFile() != null)
                .map(plugin -> plugin.getValue().getPluginFile())
                .collect(Collectors.toList()));
        return pluginFiles;
    }

    public List<File> getAllPluginFiles() {
        List<File> pluginFiles = new ArrayList<>();
        pluginFiles.addAll(getStoredPluginFiles());
        if (devPlugin != null) {
            pluginFiles.add(devPlugin.getPluginFile());
        }
        return pluginFiles;
    }

    public CustomKeywordPlugin getDevPlugin() {
        return devPlugin;
    }

    public void clearPluginInStore() {
        CustomKeywordPlugin devPlugin = customKeywordPluginCollection.get(DEV_PLUGIN_ID);
        customKeywordPluginCollection.clear();
        if (devPlugin != null) {
            customKeywordPluginCollection.put(DEV_PLUGIN_ID, devPlugin);
        }
    }

    public void clearDevPlugin() {
        devPlugin = null;
    }
}
