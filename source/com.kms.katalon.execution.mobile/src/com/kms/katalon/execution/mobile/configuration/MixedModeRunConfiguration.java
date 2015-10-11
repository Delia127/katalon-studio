package com.kms.katalon.execution.mobile.configuration;

import java.io.IOException;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IDriverConnector;

public class MixedModeRunConfiguration extends AbstractRunConfiguration {
	private IDriverConnector mobileDriverConnector;
	private IDriverConnector browserDriverConnector;

	public MixedModeRunConfiguration(TestCaseEntity testCase, IDriverConnector mobileDriverConnector,
			IDriverConnector browserDriverConnector) throws IOException {
		super(testCase);
		this.mobileDriverConnector = mobileDriverConnector;
		this.browserDriverConnector = browserDriverConnector;
	}

	public MixedModeRunConfiguration(TestSuiteEntity testSuite, IDriverConnector mobileDriverConnector,
			IDriverConnector browserDriverConnector) throws IOException {
		super(testSuite);
		this.mobileDriverConnector = mobileDriverConnector;
		this.browserDriverConnector = browserDriverConnector;
	}

	@Override
	public IDriverConnector[] getDriverConnectors() {
		return new IDriverConnector[] { mobileDriverConnector, browserDriverConnector };
	}
}
