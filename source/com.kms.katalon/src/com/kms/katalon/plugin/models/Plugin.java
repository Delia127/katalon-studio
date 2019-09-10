package com.kms.katalon.plugin.models;

import java.io.File;

public class Plugin {

    private String name;
    
    private String version;
    
    private File file;
    
    private boolean isCustomKeywordPlugin;
    
    private boolean isOnline;
    
    private KStorePlugin onlinePlugin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isCustomKeywordPlugin() {
        return isCustomKeywordPlugin;
    }

    public void setCustomKeywordPlugin(boolean isCustomKeywordPlugin) {
        this.isCustomKeywordPlugin = isCustomKeywordPlugin;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public KStorePlugin getOnlinePlugin() {
        return onlinePlugin;
    }

    public void setOnlinePlugin(KStorePlugin onlinePlugin) {
        this.onlinePlugin = onlinePlugin;
    }
}
