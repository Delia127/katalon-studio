package com.kms.katalon.execution.mobile.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.device.AndroidDeviceInfo;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;

public class AndroidRunConfiguration extends MobileRunConfiguration {
    public AndroidRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new AndroidDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }
    
    public AndroidRunConfiguration(String projectDir, MobileDriverConnector connector) throws IOException {
        super(projectDir, connector);
    }

    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new AndroidRunConfiguration(projectDir, mobileDriverConnector);
    }

    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException, ExecutionException {
        Map<String, String> environmentVariables = new HashMap<>(super.getAdditionalEnvironmentVariables());
        environmentVariables.putAll(AndroidDeviceInfo.getAndroidAdditionalEnvironmentVariables());
        return environmentVariables;
    }
}
