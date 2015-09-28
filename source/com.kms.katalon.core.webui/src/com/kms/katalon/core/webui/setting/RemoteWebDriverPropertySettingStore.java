package com.kms.katalon.core.webui.setting;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class RemoteWebDriverPropertySettingStore extends WebUiDriverPropertySettingStore {

    public RemoteWebDriverPropertySettingStore(String projectDir) throws IOException {
        super(projectDir, WebUIDriverType.REMOTE_WEB_DRIVER);
    }

    public RemoteWebDriverPropertySettingStore(String projectDir, String customProfileName)
            throws IOException {
        super(projectDir, WebUIDriverType.REMOTE_WEB_DRIVER, customProfileName);
    }

}
