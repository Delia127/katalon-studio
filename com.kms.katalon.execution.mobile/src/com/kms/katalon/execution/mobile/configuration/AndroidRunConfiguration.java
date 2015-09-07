package com.kms.katalon.execution.mobile.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;

public class AndroidRunConfiguration extends AbstractRunConfiguration {
	private IDriverConnector[] driverConnectors;
	
	public AndroidRunConfiguration(TestCaseEntity testCase, String deviceName) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new AndroidDriverConnector(deviceName) };
	}
	
	public AndroidRunConfiguration(TestSuiteEntity testSuite, String deviceName) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new AndroidDriverConnector(deviceName) };
	}
	
	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}
}
