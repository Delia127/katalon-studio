package com.kms.katalon.plugin.models;

public class ResolutionItem {

    private Plugin plugin;
    
    private Exception exception;

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
