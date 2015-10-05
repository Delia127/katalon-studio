package com.kms.katalon.execution.webui.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;
import com.kms.katalon.execution.webui.driver.SafariDriverConnector;

public class SafariRunConfiguration extends AbstractRunConfiguration {
	IDriverConnector[] driverConnectors;
	public SafariRunConfiguration(TestCaseEntity testCase) throws IOException {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new SafariDriverConnector(testCase.getProject().getFolderLocation()) };
	}

	public SafariRunConfiguration(TestSuiteEntity testSuite) throws IOException {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new SafariDriverConnector(testSuite.getProject().getFolderLocation()) };
	}
	
	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}
}
