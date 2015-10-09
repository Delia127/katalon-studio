package com.kms.katalon.composer.mobile.execution.handler;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.DeviceSelectionDialog;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.execution.mobile.exception.DeviceNameNotFoundException;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public abstract class MobileExecutionHandler extends AbstractExecutionHandler {
    protected static String getDeviceName(MobileDriverType platform) {
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

    protected static String getDeviceNameForExecution(FileEntity fileEntity, MobileDriverType mobileDriverType)
            throws Exception {
        if (fileEntity == null) {
            return null;
        }
        String deviceName = MobileExecutionUtil.getDefaultDeviceName(fileEntity, mobileDriverType);
        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = getDeviceName(mobileDriverType);
            if (deviceName == null) {
                return null;
            }
        } else if (!(MobileExecutionUtil.checkDeviceName(mobileDriverType, deviceName))) {
            throw new DeviceNameNotFoundException(MessageFormat.format(StringConstants.DIA_ERROR_CANNOT_FOUND_DEVICE_NAME,
                    deviceName));
        }
        return deviceName;
    }
}
