package com.kms.katalon.execution.mobile.configuration.contributor;

import java.util.Map;

import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.MixedModeRunConfiguration;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class MixedModeRunConfigurationContributor implements IRunConfigurationContributor {

	@Override
	public String getId() {
		return MobileDriverType.ANDROID_DRIVER.toString();
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput) {
		String deviceName = runInput
				.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME);
		String devicePlatform = runInput
				.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM);
		String browserType = runInput
				.get(com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER);
		return new MixedModeRunConfiguration(testCase, MobileExecutionUtil.getMobileDriverConnector(
				MobileDriverType.fromStringValue(devicePlatform), deviceName),
				WebUIExecutionUtil.getBrowserDriverConnector(WebUIDriverType.fromStringValue(browserType)));
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput) {
		String deviceName = runInput
				.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_DEVICE_NAME);
		String devicePlatform = runInput
				.get(com.kms.katalon.core.mobile.constants.StringConstants.CONF_EXECUTED_PLATFORM);
		String browserType = runInput
				.get(com.kms.katalon.core.webui.constants.StringConstants.CONF_PROPERTY_EXECUTED_BROWSER);
		return new MixedModeRunConfiguration(testSuite, MobileExecutionUtil.getMobileDriverConnector(
				MobileDriverType.fromStringValue(devicePlatform), deviceName),
				WebUIExecutionUtil.getBrowserDriverConnector(WebUIDriverType.fromStringValue(browserType)));
	}

}
