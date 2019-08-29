package com.kms.katalon.plugin.models;

import java.io.File;

public class OfflinePlugin {

    private String name;
    
    private File file;
    
    private boolean isCustomKeywordPlugin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
