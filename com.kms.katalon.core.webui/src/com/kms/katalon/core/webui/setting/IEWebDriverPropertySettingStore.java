package com.kms.katalon.core.webui.setting;

import java.io.IOException;

import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class IEWebDriverPropertySettingStore extends WebUiDriverPropertySettingStore {

    public IEWebDriverPropertySettingStore(String projectDir) throws IOException {
        super(projectDir, WebUIDriverType.IE_DRIVER);
    }

    public IEWebDriverPropertySettingStore(String projectDir, String customProfileName)
            throws IOException {
        super(projectDir, WebUIDriverType.IE_DRIVER, customProfileName);
    }

}
