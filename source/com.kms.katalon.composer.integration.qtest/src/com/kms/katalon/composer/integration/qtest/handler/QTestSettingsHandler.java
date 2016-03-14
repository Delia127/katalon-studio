package com.kms.katalon.composer.integration.qtest.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.PreferenceDialogBuilder;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class QTestSettingsHandler {

    @CanExecute
    public boolean canExecute() {
        if (ProjectController.getInstance().getCurrentProject() == null) {
            return false;
        }
        
        if (!LauncherManager.getInstance().isAnyLauncherRunning()) {
            return true;
        }
        return false;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        PreferenceDialog dialog = PreferenceDialogBuilder
                .create()
                .addDialogName(StringConstants.PROJECT_SETTINGS)
                .addSelectedNode(StringConstants.PREF_QTEST_MAIN_PAGE)                
                .addSize(new Point(800, 500))
                .addShell(shell)
                .build();
        dialog.open();
    }
}
