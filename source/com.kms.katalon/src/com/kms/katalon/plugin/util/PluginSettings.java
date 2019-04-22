package com.kms.katalon.plugin.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.constants.GlobalStringConstants;

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
            return new File(GlobalStringConstants.APP_USER_DIR_LOCATION, "plugin");
        }
    }
}
