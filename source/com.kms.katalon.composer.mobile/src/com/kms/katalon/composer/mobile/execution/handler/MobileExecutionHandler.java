package com.kms.katalon.composer.mobile.execution.handler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.handlers.AbstractExecutionHandler;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.dialog.DeviceSelectionDialog;
import com.kms.katalon.composer.mobile.exception.DeviceNameNotFoundException;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.execution.mobile.driver.AndroidDriverConnector;
import com.kms.katalon.execution.mobile.driver.IosDriverConnector;
import com.kms.katalon.execution.mobile.util.MobileExecutionUtil;

public abstract class MobileExecutionHandler extends AbstractExecutionHandler {
    protected String getDefaultDeviceName(FileEntity fileEntity, MobileDriverType platform) throws IOException {
        switch (platform) {
        case ANDROID_DRIVER:
            return new AndroidDriverConnector(fileEntity.getProject().getFolderLocation()).getDeviceName();
        case IOS_DRIVER:
            return new IosDriverConnector(fileEntity.getProject().getFolderLocation()).getDeviceName();
        }
        return null;
    }

    protected String getDeviceName(MobileDriverType platform) {
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

    protected String getDeviceNameForExecution(FileEntity fileEntity, MobileDriverType mobileDriverType)
            throws Exception {
        if (fileEntity == null) {
            return null;
        }
        String deviceName = getDefaultDeviceName(fileEntity, mobileDriverType);
        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = getDeviceName(mobileDriverType);
            if (deviceName == null) {
                return null;
            }
        } else if (!(checkDeviceName(mobileDriverType, deviceName))) {
            throw new DeviceNameNotFoundException(MessageFormat.format(StringConstants.DIA_ERROR_CANNOT_FOUND_DEVICE_NAME,
                    deviceName));
        }
        return deviceName;
    }

    private boolean checkDeviceName(MobileDriverType mobileDriverType, String deviceName) {
        Map<String, String> deviceMap = null;
        switch (mobileDriverType) {
        case ANDROID_DRIVER:
            try {
                deviceMap = MobileExecutionUtil.getAndroidDevices();
                for (Entry<String, String> device : deviceMap.entrySet()) {
                    if (device.getValue().equals(deviceName)) {
                        return true;
                    }
                }
            } catch (IOException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
            break;
        case IOS_DRIVER:
            try {
                deviceMap = MobileExecutionUtil.getIosDevices();
                for (Entry<String, String> device : deviceMap.entrySet()) {
                    if (device.getKey().equals(deviceName)) {
                        return true;
                    }
                }
            } catch (IOException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
            break;
        }
        return false;
    }

}
