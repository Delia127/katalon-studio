package com.kms.katalon.execution.mobile.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.mobile.configuration.IosRunConfiguration;

public class IosRunConfigurationContributor implements IRunConfigurationContributor {

	@Override
	public String getId() {
		return MobileDriverType.IOS_DRIVER.toString();
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput) throws IOException {
	    if (runInput == null
                || runInput.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME) == null) {
            return null;
        }
		String deviceName = runInput
				.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME);
		return new IosRunConfiguration(testCase, deviceName);
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput) throws IOException {
	    if (runInput == null
                || runInput.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME) == null) {
            return null;
        }
		String deviceName = runInput
				.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME);
		return new IosRunConfiguration(testSuite, deviceName);
	}

}
