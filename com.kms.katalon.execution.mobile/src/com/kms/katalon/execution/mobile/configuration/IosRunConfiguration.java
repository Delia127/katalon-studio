package com.kms.katalon.execution.mobile.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;

public class IosRunConfiguration extends AbstractRunConfiguration {
	private IDriverConnector[] driverConnectors;
	
	public IosRunConfiguration(TestCaseEntity testCase, String deviceName) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new IosDriverConnector(deviceName) };
	}
	
	public IosRunConfiguration(TestSuiteEntity testSuite, String deviceName) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new IosDriverConnector(deviceName) };
	}

	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}
}
