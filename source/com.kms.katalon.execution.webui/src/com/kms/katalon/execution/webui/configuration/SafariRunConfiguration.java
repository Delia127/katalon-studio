package com.kms.katalon.execution.webui.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;

public class SafariRunConfiguration extends AbstractRunConfiguration {
	IDriverConnector[] driverConnectors;
	public SafariRunConfiguration(TestCaseEntity testCase) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new SafariDriverConnector() };
	}

	public SafariRunConfiguration(TestSuiteEntity testSuite) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new SafariDriverConnector() };
	}
	
	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}
}
