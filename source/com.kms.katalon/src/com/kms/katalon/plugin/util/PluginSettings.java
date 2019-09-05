package com.kms.katalon.plugin.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.constants.PluginOptions;
import com.kms.katalon.execution.setting.PluginSettingStore;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class PluginSettings {

    public static File getPluginRepoDir() {
        String directoryLocation = getPluginDirectory();
        if (!StringUtils.isBlank(directoryLocation)) {
            File directory = new File(directoryLocation);
            if (directory.exists() && directory.isDirectory()) {
                return new File(directory, "plugin");
            } else {
                throw new IllegalArgumentException("Invalid repository location");
            }
        } else {
            return new File(getConfigurationFolder(), "plugin");
        }
    }

    public static PluginOptions getReloadPluginOption() throws IOException {
        PluginSettingStore pluginSettingStore = new PluginSettingStore(ProjectController.getInstance().getCurrentProject());
        return pluginSettingStore.getReloadOption();
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
