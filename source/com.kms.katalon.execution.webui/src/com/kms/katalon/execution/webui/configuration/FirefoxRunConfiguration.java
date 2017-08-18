package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;

public class FirefoxRunConfiguration extends WebUiRunConfiguration {
    public FirefoxRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new FirefoxDriverConnector(
                projectDir + File.separator + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new FirefoxRunConfiguration(projectDir);
    }

    @Override
    public boolean allowsRecording() {
        return true;
    }
}
