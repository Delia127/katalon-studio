package com.kms.katalon.composer.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
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
        try {
            new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(StringConstants.MSG_UPDATING_WEB_DRIVER, IProgressMonitor.UNKNOWN);
                    try {
                        DriverDownloadManager.downloadDriver(WebUIDriverType.FIREFOX_DRIVER);
                    } catch (InterruptedException | IOException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                };
            });
            MessageDialog.openInformation(shell, StringConstants.INFO, StringConstants.MSG_WEB_DRIVER_UPDATED_SUCCESSFULLY);
        } catch (InvocationTargetException | InterruptedException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(StringConstants.MSG_FAIL_TO_UPDATE_WEB_DRIVER, e.getMessage(), ExceptionsUtil.getStackTraceForThrowable(e));
        }
    }
}
