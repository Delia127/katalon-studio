package com.kms.katalon.composer.webui.execution.handler;

import static org.eclipse.core.runtime.Platform.getOS;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.webui.configuration.IERunConfiguration;

public class IEExecutionHandler extends AbstractExecutionHandler {

	@Override
	public boolean canExecute() {
		return (super.canExecute() && getOS().equals(Platform.OS_WIN32));
	}

	protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
		if (testCase == null) {
			return null;
		}
		return new IERunConfiguration(testCase);
	}

	protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
		if (testSuite == null) {
			return null;
		}
		return new IERunConfiguration(testSuite);
	}
}