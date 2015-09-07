package com.kms.katalon.composer.mobile.execution.handler;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.IosRunConfiguration;

public class IosExecutionHandler extends MobileExecutionHandler {

	protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
		if (testCase == null) {
			return null;
		}
		String deviceName = getDeviceName(StringConstants.OS_IOS);
		if (deviceName == null) {
			return null;
		}
		return new IosRunConfiguration(testCase, deviceName);
	}

	protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
		if (testSuite == null) {
			return null;
		}
		String deviceName = getDeviceName(StringConstants.OS_IOS);
		if (deviceName == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
					StringConstants.DIA_ERROR_NULL_DEVICE_NAME);
			return null;
		}
		return new IosRunConfiguration(testSuite, deviceName);
	}
}