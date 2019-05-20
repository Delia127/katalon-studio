package com.kms.katalon.composer.mobile.objectspy.handler;

import javax.inject.Named;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.components.KobitonAppComposite;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileObjectSpyDialog;
import com.kms.katalon.controller.ProjectController;

public class MobileSpyKobitonDeviceHandler {
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        openObjectSpyDialog(activeShell);
    }

    private boolean openObjectSpyDialog(Shell activeShell) {
        try {
            MobileObjectSpyDialog objectSpyDialog = new MobileObjectSpyDialog(activeShell, new KobitonAppComposite());

            objectSpyDialog.open();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
            return false;
        }
    }

    @CanExecute
    private boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null
                && Platform.getOS().equals(Platform.OS_MACOSX);
    }
}
