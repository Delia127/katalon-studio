package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;

public class SafariRunConfiguration extends WebUiRunConfiguration {
    public SafariRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new SafariDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDLER_NAME));
    }
    
    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new SafariRunConfiguration(projectDir);
    }
}
