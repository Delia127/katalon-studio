package com.kms.katalon.composer.webui.execution.handler;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.FirefoxRunConfiguration;

public class FirefoxExecutionHandler extends AbstractExecutionHandler {

	protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
		if (testCase == null) {
			return null;
		}
		return new FirefoxRunConfiguration(testCase);
	}

	protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
		if (testSuite == null) {
			return null;
		}
		return new FirefoxRunConfiguration(testSuite);
	}
}