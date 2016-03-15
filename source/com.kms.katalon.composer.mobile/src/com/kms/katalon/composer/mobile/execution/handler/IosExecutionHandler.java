package com.kms.katalon.composer.mobile.execution.handler;

import java.io.IOException;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.IosRunConfiguration;
import com.kms.katalon.execution.mobile.driver.MobileDevice;

public class IosExecutionHandler extends MobileExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException,
            ExecutionException, InterruptedException {
        MobileDevice deviceName = getDeviceForExecution(projectDir, MobileDriverType.IOS_DRIVER);
        if (deviceName == null) {
            return null;
        }
        
        IosRunConfiguration runConfiguration = new IosRunConfiguration(projectDir);
        runConfiguration.setDevice(deviceName);
        return runConfiguration;
    }
}