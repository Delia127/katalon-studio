package com.kms.katalon.execution.mobile.configuration;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.AbstractRunConfiguration;
import com.kms.katalon.execution.entity.IDriverConnector;

public class MixedModeRunConfiguration extends AbstractRunConfiguration {
	private IDriverConnector mobileDriverConnector;
	private IDriverConnector browserDriverConnector;

	public MixedModeRunConfiguration(TestCaseEntity testCase, IDriverConnector mobileDriverConnector,
			IDriverConnector browserDriverConnector) {
		super(testCase);
		this.mobileDriverConnector = mobileDriverConnector;
		this.browserDriverConnector = browserDriverConnector;
	}

	public MixedModeRunConfiguration(TestSuiteEntity testSuite, IDriverConnector mobileDriverConnector,
			IDriverConnector browserDriverConnector) {
		super(testSuite);
		this.mobileDriverConnector = mobileDriverConnector;
		this.browserDriverConnector = browserDriverConnector;
	}

	@Override
	public IDriverConnector[] getDriverConnectors() {
		return new IDriverConnector[] { mobileDriverConnector, browserDriverConnector };
	}
}
