package com.kms.katalon.custom.keyword;

import java.io.File;

public class CustomKeywordPlugin {

    private String id;

    private KeywordsManifest keywordsManifest;

    private File pluginFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public KeywordsManifest getKeywordsManifest() {
        return keywordsManifest;
    }

    public void setKeywordsManifest(KeywordsManifest keywordsManifest) {
        this.keywordsManifest = keywordsManifest;
    }

    public File getPluginFile() {
        return pluginFile;
    }

    public void setPluginFile(File pluginFile) {
        this.pluginFile = pluginFile;
    }
}
