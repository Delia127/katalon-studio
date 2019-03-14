package com.kms.katalon.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomKeywordPluginFactory {
    
    private static CustomKeywordPluginFactory instance;

    private List<File> pluginFiles;
    
    private CustomKeywordPluginFactory() {
        pluginFiles = new ArrayList<>();
    }
    
    public static CustomKeywordPluginFactory getInstance() {
        if (instance == null) {
            instance = new CustomKeywordPluginFactory();
        }
        return instance;
    }
    
    public void clear() {
        pluginFiles.clear();
    }
    
    public void addPluginFile(File file) {
        pluginFiles.add(file);
    }
    
    public List<File> getPluginFiles() {
        return Collections.unmodifiableList(pluginFiles);
    }
}
