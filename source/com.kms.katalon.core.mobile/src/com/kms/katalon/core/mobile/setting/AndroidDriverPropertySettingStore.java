package com.kms.katalon.core.mobile.setting;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;

public class AndroidDriverPropertySettingStore extends MobileDriverPropertySettingStore {

    public AndroidDriverPropertySettingStore(String projectDir) throws IOException {
        super(projectDir, MobileDriverType.ANDROID_DRIVER);
    }

    public AndroidDriverPropertySettingStore(String projectDir, String customProfileName)
            throws IOException {
        super(projectDir, MobileDriverType.ANDROID_DRIVER, customProfileName);
    }
}
