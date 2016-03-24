package com.kms.katalon.composer.mobile.execution.handler;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.DeviceSelectionDialog;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.driver.MobileDevice;
import com.kms.katalon.execution.mobile.exception.DeviceNameNotFoundException;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public abstract class MobileExecutionHandler extends AbstractExecutionHandler {
    protected static MobileDevice getDevice(MobileDriverType platform) {
        DeviceSelectionDialog dialog = new DeviceSelectionDialog(Display.getCurrent().getActiveShell(), platform);
        dialog.open();
        MobileDevice device = null;
        if (dialog.getReturnCode() == Dialog.OK) {
            device = dialog.getDevice();
            if (device == null) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                        StringConstants.DIA_ERROR_NULL_DEVICE_NAME);
            }
        }
        return device;
    }

    protected static MobileDevice getDeviceForExecution(String projectDir, MobileDriverType mobileDriverType)
            throws IOException, DeviceNameNotFoundException, InterruptedException {
        String deviceId = MobileExecutionUtil.getDefaultDeviceId(projectDir, mobileDriverType);
        MobileDevice device = null;
        if (StringUtils.isBlank(deviceId)) {
            device = getDevice(mobileDriverType);
        } else {
            device = MobileExecutionUtil.getDevice(mobileDriverType, deviceId);
            if (device == null) {
                throw new DeviceNameNotFoundException(MessageFormat.format(
                        StringConstants.DIA_ERROR_CANNOT_FOUND_DEVICE_NAME, deviceId));
            }
        }
        return device;
    }
}
