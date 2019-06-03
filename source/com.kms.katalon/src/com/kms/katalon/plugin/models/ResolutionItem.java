package com.kms.katalon.plugin.models;

import java.io.File;

public class ResolutionItem {

    private KStorePlugin plugin;
    
    private Exception exception;

    public KStorePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(KStorePlugin plugin) {
        this.plugin = plugin;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
