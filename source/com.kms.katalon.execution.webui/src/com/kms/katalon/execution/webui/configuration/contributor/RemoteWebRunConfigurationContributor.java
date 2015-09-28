package com.kms.katalon.execution.webui.configuration.contributor;

import java.util.Map;

import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.constants.StringConstants;
import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.IRunConfiguration;

public class RemoteWebRunConfigurationContributor implements IRunConfigurationContributor {

	@Override
	public String getId() {
		return WebUIDriverType.REMOTE_WEB_DRIVER.toString();
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput) {
		String remoteWebDriverUrl = runInput.get(DriverFactory.REMOTE_WEB_DRIVER_URL);
		if (remoteWebDriverUrl == null) {
			throw new IllegalArgumentException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE);
		}
		return new RemoteWebRunConfiguration(testCase, remoteWebDriverUrl);
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput) {
		String remoteWebDriverUrl = runInput.get(DriverFactory.REMOTE_WEB_DRIVER_URL);
		if (remoteWebDriverUrl == null) {
			throw new IllegalArgumentException(StringConstants.REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE);
		}
		return new RemoteWebRunConfiguration(testSuite, remoteWebDriverUrl);
	}

}
