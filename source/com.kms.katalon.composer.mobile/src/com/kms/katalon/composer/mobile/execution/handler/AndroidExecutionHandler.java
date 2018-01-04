package com.kms.katalon.composer.mobile.execution.handler;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.util.MobileDeviceUIProvider;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.AndroidRunConfiguration;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class AndroidExecutionHandler extends MobileExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir)
            throws IOException, ExecutionException, InterruptedException {
        Shell activeShell = Display.getCurrent().getActiveShell();
        if (!MobileDeviceUIProvider.checkAndroidSDKExist(activeShell)) {
            return null;
        }
        MobileDeviceInfo deviceInfo = null;
        try {
            deviceInfo = getDeviceForExecution(projectDir, MobileDriverType.ANDROID_DRIVER);
        } catch (MobileSetupException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openInformation(activeShell, "Error", e.getClass().getName() + ": " + e.getMessage());
        }
        if (deviceInfo == null) {
            return null;
        }
        AndroidRunConfiguration runConfiguration = new AndroidRunConfiguration(projectDir);
        runConfiguration.setDevice(deviceInfo);
        return runConfiguration;
    }
}
