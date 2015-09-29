package com.kms.katalon.core.mobile.setting;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class IosDriverPropertySettingStore extends MobileDriverPropertySettingStore {

    public IosDriverPropertySettingStore(String projectDir) throws IOException {
        super(projectDir, MobileDriverType.IOS_DRIVER);
    }

    public IosDriverPropertySettingStore(String projectDir, String customProfileName)
            throws IOException {
        super(projectDir, MobileDriverType.IOS_DRIVER, customProfileName);
    }
}
