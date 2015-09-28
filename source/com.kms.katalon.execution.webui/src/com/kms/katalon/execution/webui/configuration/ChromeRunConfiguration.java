package com.kms.katalon.execution.webui.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.webui.driver.ChromeDriverConnector;

public class ChromeRunConfiguration extends AbstractRunConfiguration {
	IDriverConnector[] driverConnectors;
	public ChromeRunConfiguration(TestCaseEntity testCase) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new ChromeDriverConnector() };
	}

	public ChromeRunConfiguration(TestSuiteEntity testSuite) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new ChromeDriverConnector() };
	}

	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}

}
