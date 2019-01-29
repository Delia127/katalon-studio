package com.kms.katalon.plugin.models;

public class ResultItem {

    private KStorePlugin plugin;
    
    private boolean isPluginInstalled;
    
    private boolean isNewVersionAvailable;

    public KStorePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(KStorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPluginInstalled() {
        return isPluginInstalled;
    }

    public void markPluginInstalled(boolean installed) {
        this.isPluginInstalled = installed;
    }

    public boolean isNewVersionAvailable() {
        return isNewVersionAvailable;
    }

    public void setNewVersionAvailable(boolean isNewVersionAvailable) {
        this.isNewVersionAvailable = isNewVersionAvailable;
    }

    public void setPluginInstalled(boolean isPluginInstalled) {
        this.isPluginInstalled = isPluginInstalled;
    }
}
