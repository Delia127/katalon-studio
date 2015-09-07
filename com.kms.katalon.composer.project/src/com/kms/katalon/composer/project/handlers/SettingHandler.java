package com.kms.katalon.composer.project.handlers;

import javax.inject.Named;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferencesRegistry;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class SettingHandler {
	
	@CanExecute
	public boolean canExecute() {
		try {
			return (ProjectController.getInstance().getCurrentProject() != null && !LauncherManager.getInstance()
					.isAnyLauncherRunning());
		} catch (CoreException e) {
			return false;
		}
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			PreferencesRegistry preferencesRegistry) {
		PreferenceManager pm = preferencesRegistry.getPreferenceManager(PreferencesRegistry.PREFS_PROJECT_XP);
		PreferenceDialog dialog = new PreferenceDialog(shell, pm);
		dialog.create();
		dialog.getTreeViewer().setComparator(new ViewerComparator());
		dialog.getTreeViewer().expandToLevel(2);
		dialog.getShell().setText(StringConstants.HAND_PROJ_SETTING);
		dialog.getShell().setSize(800, 500);
		dialog.open();		
	}
}
