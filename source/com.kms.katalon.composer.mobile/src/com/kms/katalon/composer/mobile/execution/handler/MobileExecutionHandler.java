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
import com.kms.katalon.composer.mobile.util.MobileUtil;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.configuration.contributor.MobileRunConfigurationContributor;
import com.kms.katalon.execution.mobile.configuration.providers.MobileDeviceProvider;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.DeviceNameNotFoundException;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public abstract class MobileExecutionHandler extends AbstractExecutionHandler {
    protected static MobileDeviceInfo getDevice(MobileDriverType platform) {
        MobileUtil.detectAppiumAndNodeJs(Display.getCurrent().getActiveShell());
        DeviceSelectionDialog dialog = new DeviceSelectionDialog(Display.getCurrent().getActiveShell(), platform);
        dialog.open();
        if (dialog.getReturnCode() != Dialog.OK) {
            return null;
        }
        MobileDeviceInfo device = dialog.getDevice();
        if (device == null) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                    StringConstants.DIA_ERROR_NULL_DEVICE_NAME);
            return null;
        }
        return device;
    }

    protected static MobileDeviceInfo getDeviceForExecution(String projectDir, MobileDriverType mobileDriverType)
            throws IOException, DeviceNameNotFoundException, InterruptedException, MobileSetupException {
        String deviceId = MobileRunConfigurationContributor.getDefaultDeviceId(projectDir, mobileDriverType);
        if (StringUtils.isBlank(deviceId)) {
            return getDevice(mobileDriverType);
        }
        MobileDeviceInfo device = MobileDeviceProvider.getDevice(mobileDriverType, deviceId);
        if (device == null) {
            throw new DeviceNameNotFoundException(MessageFormat.format(
                    StringConstants.DIA_ERROR_CANNOT_FOUND_DEVICE_NAME, deviceId));
        }
        return device;
    }

}
