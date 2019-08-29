package com.kms.katalon.plugin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.plugin.models.KStorePlugin;
import com.kms.katalon.plugin.models.Plugin;

public class PluginFactory {

    private static PluginFactory instance;
    
    private List<Plugin> plugins;
    
    private PluginFactory() {
        plugins = new ArrayList<>();
    }
    
    public static PluginFactory getInstance() {
        if (instance == null) {
            instance = new PluginFactory();
        }
        return instance;
    }
    
    public List<Plugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }
    
    public void addPlugin(Plugin plugin) {
        plugins.add(plugin);
    }
    
    public void clear() {
        plugins.clear();
    }
}
