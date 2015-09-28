package com.kms.katalon.execution.webui.configuration;

import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;

public class RemoteWebRunConfiguration extends AbstractRunConfiguration {
	IDriverConnector[] driverConnectors;
	private String remoteWebServerUrl;
	public RemoteWebRunConfiguration(TestCaseEntity testCase, String remoteWebServerUrl) {
		super(testCase);
		driverConnectors = new IDriverConnector[] { new RemoteWebDriverConnector(remoteWebServerUrl) };
		this.remoteWebServerUrl = remoteWebServerUrl;
	}

	public RemoteWebRunConfiguration(TestSuiteEntity testSuite, String remoteWebServerUrl) {
		super(testSuite);
		driverConnectors = new IDriverConnector[] { new RemoteWebDriverConnector(remoteWebServerUrl) };
		this.remoteWebServerUrl = remoteWebServerUrl;
	}

	@Override
	public IDriverConnector[] getDriverConnectors() {
		return driverConnectors;
	}
	
	@Override
	public String getName() {
		return super.getName() + " - " + remoteWebServerUrl;
	}

}
