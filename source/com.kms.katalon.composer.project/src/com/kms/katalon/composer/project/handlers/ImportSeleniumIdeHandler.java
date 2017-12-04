package com.kms.katalon.composer.project.handlers;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.selenium.ide.ImportSeleniumIdeService;

public class ImportSeleniumIdeHandler {
	
	@CanExecute
	public boolean canExecute() {
	    return (ProjectController.getInstance().getCurrentProject() != null)
                && !LauncherManager.getInstance().isAnyLauncherRunning();
	}
	
	@Execute
	public void execute() {
		try {
			Shell shell = Display.getCurrent().getActiveShell();
			FileDialog fileDialog = new FileDialog(shell, SWT.SYSTEM_MODAL);
			fileDialog.setText(StringConstants.HAND_IMPORT_SELENIUM_IDE);
			fileDialog.setFilterPath(Platform.getLocation().toString());
			String selectedFile = fileDialog.open();
			if (selectedFile != null && selectedFile.length() > 0) {
				File testSuiteFile = new File(selectedFile);
				if (testSuiteFile != null && testSuiteFile.exists()) {
					ImportSeleniumIdeService.getInstance().importFile(testSuiteFile);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
