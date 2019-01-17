package com.kms.katalon.plugin.models;

public class ResultItem {

    private KStorePlugin plugin;
    
    private boolean pluginInstalled;

    public KStorePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(KStorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPluginInstalled() {
        return pluginInstalled;
    }

    public void markPluginInstalled(boolean installed) {
        this.pluginInstalled = installed;
    }
}
