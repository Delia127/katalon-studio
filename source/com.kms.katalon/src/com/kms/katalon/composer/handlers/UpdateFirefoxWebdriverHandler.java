package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.exception.RunInstallationStepException;
import com.kms.katalon.composer.components.impl.installer.InstallationManager;
import com.kms.katalon.composer.components.impl.installer.InstallationStep;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class UpdateFirefoxWebdriverHandler {

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(Shell shell) {
        InstallationManager installationManager = new InstallationManager(shell, StringConstants.MSG_UPDATING_WEB_DRIVER);
        installationManager.getInstallationDialog().setDialogTitle(MessageFormat.format(StringConstants.DIA_TITLE_UPDATE_WEBDRIVER, "FireFox"));
        installationManager.getInstallationDialog().setSucceededMessage(StringConstants.MSG_WEB_DRIVER_UPDATED_SUCCESSFULLY);

        InstallationStep installHomebrewStep = new InstallationStep(StringConstants.MSG_UPDATING_WEB_DRIVER) {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    DriverDownloadManager.downloadDriver(WebUIDriverType.FIREFOX_DRIVER, getLogFile(), getErrorLogFile());
                } catch (IOException error) {
                    throw new RunInstallationStepException(StringConstants.MSG_FAIL_TO_UPDATE_WEB_DRIVER, error);
                }
            }
        };
        installationManager.appendStep(installHomebrewStep);

        try {
            installationManager.startInstallation();
        } catch (InvocationTargetException error) {
            LoggerSingleton.logError(error);
            MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_FAIL_TO_UPDATE_WEB_DRIVER,
                    error.getMessage(), ExceptionsUtil.getStackTraceForThrowable(error));
        } catch (InterruptedException error) {
            LoggerSingleton.logInfo(StringConstants.MSG_USER_CANCEL_UPDATE);
        }
    }
}
