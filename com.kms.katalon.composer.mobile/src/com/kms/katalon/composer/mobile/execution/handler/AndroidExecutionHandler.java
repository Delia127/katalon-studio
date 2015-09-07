package com.kms.katalon.composer.mobile.execution.handler;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;

public class AndroidExecutionHandler extends MobileExecutionHandler {

	protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
		if (testCase == null) {
			return null;
		}
		String deviceName = getDeviceName(StringConstants.OS_ANDROID);
		if (deviceName == null) {
			return null;
		}
		return new AndroidRunConfiguration(testCase, deviceName);
	}

	protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
		if (testSuite == null) {
			return null;
		}
		String deviceName = getDeviceName(StringConstants.OS_ANDROID);
		if (deviceName == null) {
			return null;
		}
		return new AndroidRunConfiguration(testSuite, deviceName);
	}
}