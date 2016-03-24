package com.kms.katalon.execution.mobile.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidRunConfiguration extends MobileRunConfiguration {
    public AndroidRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new AndroidDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new AndroidRunConfiguration(projectDir);
    }
}
