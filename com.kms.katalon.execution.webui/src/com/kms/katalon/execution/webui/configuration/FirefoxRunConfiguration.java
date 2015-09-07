package com.kms.katalon.execution.webui.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.webui.driver.FirefoxDriverConnector;

public class FirefoxRunConfiguration extends AbstractRunConfiguration {
	IDriverConnector[] driverConnectors;
	public FirefoxRunConfiguration(TestCaseEntity testCase) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new FirefoxDriverConnector() };
	}

	public FirefoxRunConfiguration(TestSuiteEntity testSuite) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new FirefoxDriverConnector() };
	}

	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}

}
