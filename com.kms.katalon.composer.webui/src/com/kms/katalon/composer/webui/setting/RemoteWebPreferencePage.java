package com.kms.katalon.composer.webui.setting;

import java.io.IOException;

import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.core.setting.DriverPropertySettingStore;
import com.kms.katalon.core.webui.setting.RemoteWebDriverPropertySettingStore;

public class RemoteWebPreferencePage extends DriverPreferencePage {

    @Override
    protected DriverPropertySettingStore getDriverPropertySettingStore(String projectFolderLocation) {
        try {
            return new RemoteWebDriverPropertySettingStore(projectFolderLocation);
        } catch (IOException e) {
            // IO Errors, return null
            return null;
        }
    }
}
