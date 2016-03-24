package com.kms.katalon.execution.mobile.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosRunConfiguration extends MobileRunConfiguration {
    public IosRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new IosDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }
    
    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new IosRunConfiguration(projectDir);
    }
}
