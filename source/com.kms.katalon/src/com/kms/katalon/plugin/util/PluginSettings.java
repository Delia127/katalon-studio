package com.kms.katalon.plugin.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

public class PluginSettings {

    public static File getPluginRepoDir() {
        String location = System.getProperty("pluginRepository");
        if (!StringUtils.isBlank(location)) {
            File directory = new File(location);
            if (directory.exists() && directory.isDirectory()) {
                return new File(directory, "plugin");
            } else {
                throw new IllegalArgumentException("Invalid repository location");
            }
        } else {
            return new File(getConfigurationFolder(), "plugin");
        }
    }
    
    private static File getConfigurationFolder() {
        try {
            return new File(FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
        } catch (IOException e) {
        }
        return null;
    }
}
