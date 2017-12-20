package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.driver.FirefoxHeadlessDriverConnector;

public class FirefoxHeadlessRunConfiguration extends WebUiRunConfiguration {

    public FirefoxHeadlessRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new FirefoxHeadlessDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new FirefoxHeadlessRunConfiguration(projectDir);
    }
}
