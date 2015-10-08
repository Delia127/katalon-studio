package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.core.setting.DriverPropertySettingStore;
import com.kms.katalon.core.webui.setting.FirefoxDriverPropertySettingStore;

public class FirefoxPerferencePage extends DriverPreferencePage {

    @Override
    protected DriverPropertySettingStore getDriverPropertySettingStore(String projectFolderLocation) {
        try {
            return new FirefoxDriverPropertySettingStore(projectFolderLocation);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            // IO Errors, return null
            return null;
        }
    }
}
