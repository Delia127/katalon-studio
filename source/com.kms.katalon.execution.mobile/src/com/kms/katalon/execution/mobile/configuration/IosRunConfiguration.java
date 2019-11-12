package com.kms.katalon.execution.mobile.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.setting.PropertySettingStoreUtil;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;
import com.kms.katalon.execution.mobile.driver.MobileDriverConnector;
import com.kms.katalon.logging.LogUtil;

public class IosRunConfiguration extends MobileRunConfiguration {
    public IosRunConfiguration(String projectDir) throws IOException {
        super(projectDir, new IosDriverConnector(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME));
    }
    
    public IosRunConfiguration(String projectDir, MobileDriverConnector mobileDriverConnector) throws IOException {
        super(projectDir, mobileDriverConnector);
    }
    
    @Override
    public IRunConfiguration cloneConfig() throws IOException {
        return new IosRunConfiguration(projectDir, mobileDriverConnector);
    }
    
    @Override
    public Map<String, String> getAdditionalEnvironmentVariables() throws IOException {
        try {
            Map<String, String> environmentVariables = new HashMap<String, String>(super.getAdditionalEnvironmentVariables());
            environmentVariables.putAll(IosDeviceInfo.getIosAdditionalEnvironmentVariables());
            return environmentVariables;
        } catch (InterruptedException | ExecutionException e) {
            LogUtil.logError(e);
            return Collections.emptyMap();
        }
    }
}
