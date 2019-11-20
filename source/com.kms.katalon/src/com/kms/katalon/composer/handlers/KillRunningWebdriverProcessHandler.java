package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.exception.RunInstallationStepException;
import com.kms.katalon.composer.components.impl.installer.InstallationManager;
import com.kms.katalon.composer.components.impl.installer.InstallationStep;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.webui.util.WebDriverCleanerUtil;

public class KillRunningWebdriverProcessHandler {

    @Execute
    public void execute(Shell shell) {
        InstallationManager installationManager = new InstallationManager(shell, StringConstants.MSG_TERMINATING_WEB_DRIVER);
        installationManager.getInstallationDialog().setDialogTitle(StringConstants.DIA_TITLE_TERMINATE_WEB_DRIVER);
        installationManager.getInstallationDialog()
                .setSuccessfulMessage(StringConstants.MSG_WEB_DRIVER_TERMINATE_SUCCESSFULLY);

        InstallationStep installationStep = new InstallationStep(StringConstants.MSG_TERMINATING_WEB_DRIVER) {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    WebDriverCleanerUtil.cleanup(getLogFile(), getLogFile());
                } catch (IOException error) {
                    throw new RunInstallationStepException(StringConstants.MSG_FAIL_TO_KILL_WEB_DRIVER, error);
                }
            }
        };
        installationManager.appendStep(installationStep);

        try {
            installationManager.startInstallation();
        } catch (InvocationTargetException error) {
            LoggerSingleton.logError(error);
            MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_FAIL_TO_KILL_WEB_DRIVER, error.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(error));
        } catch (InterruptedException error) {
            LoggerSingleton.logInfo(StringConstants.MSG_USER_CANCEL_WEB_DRIVER_TERMINATION);
        }
    }
}
