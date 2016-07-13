package com.kms.katalon.composer.mobile.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileObjectSpyDialog;
import com.kms.katalon.controller.ProjectController;

public class MobileSpyMobileHandler {
    private MobileObjectSpyDialog objectSpyDialog;

    private Shell activeShell;

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        try {
            if (this.activeShell == null) {
                this.activeShell = activeShell;
            }

            if (!isObjectSpyDialogRunning()) {
                objectSpyDialog = new MobileObjectSpyDialog(activeShell);
                objectSpyDialog.open();
            }
            
            if (!objectSpyDialog.isCanceledBeforeOpening()) {
                objectSpyDialog.getShell().forceActive();
            }
        } catch (Exception e) {
            if (isObjectSpyDialogRunning()) {
                objectSpyDialog.dispose();
                objectSpyDialog.close();
            }
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    @CanExecute
    private boolean canExecute() throws Exception {
        return ProjectController.getInstance().getCurrentProject() != null;
    }


    public boolean isObjectSpyDialogRunning() {
        return objectSpyDialog != null && !objectSpyDialog.isDisposed();
    }
}
