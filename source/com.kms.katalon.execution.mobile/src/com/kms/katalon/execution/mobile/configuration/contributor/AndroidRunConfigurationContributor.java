package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;

public class AndroidRunConfigurationContributor implements IRunConfigurationContributor {

    @Override
    public String getId() {
        return MobileDriverType.ANDROID_DRIVER.toString();
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput)
            throws IOException, ExecutionException {
        String deviceName = null;
        if (runInput != null) {
            deviceName = runInput.get(StringConstants.CONF_EXECUTED_DEVICE_NAME);
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(testCase);
        if (deviceName != null) {
            runConfiguration.setDeviceName(deviceName);
        }
        if (runConfiguration.getDeviceName() == null || runConfiguration.getDeviceName().isEmpty()) {
            throw new ExecutionException(
                    com.kms.katalon.execution.mobile.constants.StringConstants.MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        return runConfiguration;
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException, ExecutionException {
        String deviceName = null;
        if (runInput != null) {
            deviceName = runInput.get(StringConstants.CONF_EXECUTED_DEVICE_NAME);
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(testSuite);
        if (deviceName != null) {
            runConfiguration.setDeviceName(deviceName);
        }
        if (runConfiguration.getDeviceName() == null || runConfiguration.getDeviceName().isEmpty()) {
            throw new ExecutionException(
                    com.kms.katalon.execution.mobile.constants.StringConstants.MOBILE_ERR_NO_DEVICE_NAME_AVAILABLE);
        }
        return runConfiguration;
    }
    
    @Override
    public int getPreferredOrder() {
        return 6;
    }

}
