package com.kms.katalon.plugin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.plugin.models.KStorePlugin;

public class PluginFactory {

    private static PluginFactory instance;
    
    private List<KStorePlugin> plugins;
    
    private PluginFactory() {
        plugins = new ArrayList<>();
    }
    
    public static PluginFactory getInstance() {
        if (instance == null) {
            instance = new PluginFactory();
        }
        return instance;
    }
    
    public List<KStorePlugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }
    
    public void addPlugin(KStorePlugin plugin) {
        plugins.add(plugin);
    }
    
    public void clear() {
        plugins.clear();
    }
}
