package com.kms.katalon.composer.mobile.execution.handler;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;

public class AndroidExecutionHandler extends MobileExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
        if (testCase == null) {
            return null;
        }
        String deviceName = getDeviceNameForExecution(testCase, MobileDriverType.ANDROID_DRIVER);
        if (deviceName == null || deviceName.isEmpty()) {
            return null;
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(testCase);
        runConfiguration.setDeviceName(deviceName);
        return runConfiguration;
    }

    protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
        if (testSuite == null) {
            return null;
        }
        String deviceName = getDeviceNameForExecution(testSuite, MobileDriverType.ANDROID_DRIVER);
        if (deviceName == null || deviceName.isEmpty()) {
            return null;
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(testSuite);
        runConfiguration.setDeviceName(deviceName);
        return runConfiguration;
    }
}