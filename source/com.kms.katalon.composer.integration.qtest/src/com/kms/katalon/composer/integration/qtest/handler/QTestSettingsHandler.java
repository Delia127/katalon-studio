package com.kms.katalon.composer.integration.qtest.handler;

import javax.inject.Named;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.preferences.internal.PreferencesRegistry;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class QTestSettingsHandler {

    @CanExecute
    public boolean canExecute() {
        if (ProjectController.getInstance().getCurrentProject() == null) {
            return false;
        }
        try {
            if (!LauncherManager.getInstance().isAnyLauncherRunning()) {
                return true;
            }
        } catch (CoreException e) {

        }
        return false;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, PreferencesRegistry preferencesRegistry) {
        PreferenceManager pm = preferencesRegistry.getPreferenceManager(PreferencesRegistry.PREFS_PROJECT_XP);
        PreferenceDialog dialog = new PreferenceDialog(shell, pm);
        dialog.setSelectedNode("com.kms.katalon.composer.intergration.qtest.setting");
        dialog.create();
        dialog.getTreeViewer().setComparator(new ViewerComparator());
        dialog.getTreeViewer().expandToLevel(3);
        dialog.getShell().setText("Project Settings");
        dialog.getShell().setSize(800, 500);
        dialog.open();
    }
}
