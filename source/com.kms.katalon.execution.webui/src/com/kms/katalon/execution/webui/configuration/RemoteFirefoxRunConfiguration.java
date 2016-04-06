package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.driver.RemoteFirefoxDriverConnector;

public class RemoteFirefoxRunConfiguration extends WebUiRunConfiguration {
	
    public RemoteFirefoxRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new RemoteFirefoxDriverConnector(projectDir + File.separator 
        		+ PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }
    
    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new RemoteFirefoxRunConfiguration(projectDir);
    }
}
