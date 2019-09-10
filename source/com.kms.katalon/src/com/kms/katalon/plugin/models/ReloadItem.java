package com.kms.katalon.plugin.models;

public class ReloadItem {

    private Plugin plugin;
    
    private boolean isPluginInstalled;
    
    private boolean isNewVersionAvailable;
    
    private Exception exception;

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
