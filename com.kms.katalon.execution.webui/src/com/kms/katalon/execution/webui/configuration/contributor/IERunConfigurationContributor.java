package com.kms.katalon.execution.webui.configuration.contributor;

import java.util.Map;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.IERunConfiguration;

public class IERunConfigurationContributor implements IRunConfigurationContributor {

	@Override
	public String getId() {
		return WebUIDriverType.IE_DRIVER.toString();
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput) {
		return new IERunConfiguration(testCase);
	}

	@Override
	public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput) {
		return new IERunConfiguration(testSuite);
	}

}
