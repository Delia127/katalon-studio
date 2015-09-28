package com.kms.katalon.composer.mobile.execution.handler;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.DeviceSelectionDialog;

public abstract class MobileExecutionHandler extends AbstractExecutionHandler {
	protected String getDeviceName(String platform) {
		DeviceSelectionDialog dialog = new DeviceSelectionDialog(Display.getCurrent().getActiveShell(), platform);
		dialog.open();
		if (dialog.getReturnCode() == Dialog.OK) {
			String deviceName = dialog.getDeviceName();
			if (deviceName == null || deviceName.equals("")) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
						StringConstants.DIA_ERROR_NULL_DEVICE_NAME);
				return null;
			}
			return deviceName;
		}
		return null;
	}

}
