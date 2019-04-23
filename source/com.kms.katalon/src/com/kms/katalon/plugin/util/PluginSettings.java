package com.kms.katalon.plugin.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class PluginSettings {

    public static File getPluginRepoDir() {
        String pluginDirectory = getPluginDirectory();
        if (!StringUtils.isBlank(pluginDirectory)) {
            File directory = new File(pluginDirectory);
            if (directory.exists() && directory.isDirectory()) {
                return new File(directory, "plugin");
            } else {
                throw new IllegalArgumentException("Invalid repository location");
            }
        } else {
            return new File(getConfigurationFolder(), "plugin");
        }
    }

    private static String getPluginDirectory() {
        IPreferenceStore preferenceStore = PreferenceStoreManager
                .getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
        return preferenceStore.getString(PreferenceConstants.PLUGIN_DIRECTORY);
    }

    private static File getConfigurationFolder() {
        try {
            return new File(FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
        } catch (IOException e) {
        }
        return null;
    }
}
