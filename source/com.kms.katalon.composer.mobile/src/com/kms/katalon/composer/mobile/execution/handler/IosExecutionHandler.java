package com.kms.katalon.composer.mobile.execution.handler;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.execution.mobile.configuration.IosRunConfiguration;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public class IosExecutionHandler extends MobileExecutionHandler {

    protected IRunConfiguration getRunConfigurationForExecution(String projectDir) throws IOException,
            ExecutionException, InterruptedException {
        MobileDeviceInfo device = null;
        try {
            device = getDeviceForExecution(projectDir, MobileDriverType.IOS_DRIVER);
        } catch (MobileSetupException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Error", e.getClass().getName() + ": "
                    + e.getMessage());
        }
        if (device == null) {
            return null;
        }

        IosRunConfiguration runConfiguration = new IosRunConfiguration(projectDir);
        runConfiguration.setDevice(device);
        return runConfiguration;
    }
}