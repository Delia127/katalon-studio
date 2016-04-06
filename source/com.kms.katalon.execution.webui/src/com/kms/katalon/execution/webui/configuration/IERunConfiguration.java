package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;

public class IERunConfiguration extends WebUiRunConfiguration {
    public IERunConfiguration(String projectDir) throws IOException {
        super(projectDir, new IEDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new IERunConfiguration(projectDir);
    }
}
