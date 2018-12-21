package com.kms.katalon.plugin.models;

public class ResultItem {

    private KStorePlugin plugin;
    
    private boolean installed;

    public KStorePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(KStorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
}
