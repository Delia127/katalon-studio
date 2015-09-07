package com.kms.katalon.composer.mobile.execution.handler;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.MixedModeSelectionDialog;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.mobile.configuration.MixedModeRunConfiguration;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;
import com.kms.katalon.execution.webui.util.WebUIExecutionUtil;

public class MixedModeExecutionHandler extends MobileExecutionHandler {

	@Override
	protected IRunConfiguration getRunConfigurationForExecution(TestCaseEntity testCase) throws Exception {
		return getRunConfigurationForExecutionForFileEntity(testCase);
	}

	private IRunConfiguration getRunConfigurationForExecutionForFileEntity(FileEntity fileEntity) throws Exception {
		if (fileEntity == null || !(fileEntity instanceof TestCaseEntity || fileEntity instanceof TestSuiteEntity)) {
			return null;
		}
		MixedModeSelectionDialog dialog = new MixedModeSelectionDialog(Display.getCurrent().getActiveShell());
		dialog.open();
		if (dialog.getReturnCode() == Dialog.OK) {
			String deviceName = dialog.getDeviceName();
			String browserName = dialog.getBrowserName();
			if ((deviceName == null || deviceName.equals("")) && (browserName == null || browserName.equals(""))) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.WARN,
						StringConstants.DIA_ERROR_NULL_DEVICE_BROWSER_NAME);
				return null;
			} else {
				WebUIDriverType webDriverType = null;
				MobileDriverType mobileDriverType = null;
				if (deviceName != null && !deviceName.equals("")) {
					mobileDriverType = deviceName.endsWith("(" + StringConstants.OS_IOS + ")") ? MobileDriverType.IOS_DRIVER
							: MobileDriverType.ANDROID_DRIVER;
					deviceName = deviceName.substring(0, deviceName.length()
							- ("(" + mobileDriverType.getPlatform() + ")").length());
					deviceName = deviceName.trim();
				}
				if (browserName != null && !browserName.equals("")) {
					webDriverType = WebUIDriverType.fromStringValue(browserName);
				}
				if (fileEntity instanceof TestCaseEntity) {
					return new MixedModeRunConfiguration((TestCaseEntity) fileEntity, MobileExecutionUtil.getMobileDriverConnector(
							mobileDriverType, deviceName), WebUIExecutionUtil.getBrowserDriverConnector(webDriverType));
				} else if (fileEntity instanceof TestSuiteEntity) {
					return new MixedModeRunConfiguration((TestSuiteEntity) fileEntity, MobileExecutionUtil.getMobileDriverConnector(
							mobileDriverType, deviceName), WebUIExecutionUtil.getBrowserDriverConnector(webDriverType));
				}

			}

		}
		return null;
	}

	protected IRunConfiguration getRunConfigurationForExecution(TestSuiteEntity testSuite) throws Exception {
		return getRunConfigurationForExecutionForFileEntity(testSuite);
	}

}