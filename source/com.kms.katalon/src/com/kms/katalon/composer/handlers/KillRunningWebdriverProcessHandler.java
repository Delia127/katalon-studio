package com.kms.katalon.composer.handlers;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;

public class KillRunningWebdriverProcessHandler {
	@Execute
	public void execute(Shell shell) {
		try {
			WebDriverCleanerUtil.cleanup();
		} catch (InterruptedException e) {
			LoggerSingleton.logError(e, e.getMessage());
			MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_FAIL_TO_KILL_WEB_DRIVER, e.getMessage(),
					ExceptionsUtil.getStackTraceForThrowable(e));
			return;
		} catch (IOException e) {
			LoggerSingleton.logError(e, e.getMessage());
			MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_FAIL_TO_KILL_WEB_DRIVER, e.getMessage(),
					ExceptionsUtil.getStackTraceForThrowable(e));
			return;
		}

		MessageDialog.openInformation(shell, "Information", StringConstants.MSG_WEB_DRIVER_TERMINATE_SUCCESSFULLY);
	}
}
