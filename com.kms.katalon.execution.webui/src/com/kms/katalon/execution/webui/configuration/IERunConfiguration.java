package com.kms.katalon.execution.webui.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.webui.driver.IEDriverConnector;

public class IERunConfiguration extends AbstractRunConfiguration {
	IDriverConnector[] driverConnectors;
	public IERunConfiguration(TestCaseEntity testCase) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new IEDriverConnector() };
	}

	public IERunConfiguration(TestSuiteEntity testSuite) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new IEDriverConnector() };
	}
	
	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}

}
