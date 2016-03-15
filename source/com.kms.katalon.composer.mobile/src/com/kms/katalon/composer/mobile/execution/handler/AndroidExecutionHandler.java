package com.kms.katalon.composer.mobile.execution.handler;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;
import com.kms.katalon.execution.mobile.driver.MobileDevice;

public class AndroidExecutionHandler extends MobileExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException,
            ExecutionException, InterruptedException {
        MobileDevice deviceName = getDeviceForExecution(projectDir, MobileDriverType.ANDROID_DRIVER);
        if (deviceName == null) {
            return null;
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(projectDir);
        runConfiguration.setDevice(deviceName);
        return runConfiguration;
    }
}