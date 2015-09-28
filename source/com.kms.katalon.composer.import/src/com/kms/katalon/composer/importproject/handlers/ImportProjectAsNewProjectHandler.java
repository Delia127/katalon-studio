package com.kms.katalon.composer.importproject.handlers;

import java.io.File;
import java.util.UUID;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.importproject.constants.StringConstants;
import com.kms.katalon.controller.ImportProjectAsNewProjectController;

public class ImportProjectAsNewProjectHandler {
	private static final String PROJECT_NAME_DIALOG_MESSAGE = StringConstants.HAND_NEW_PROJECT_NAME_MSG;
	private static final String PROJECT_NAME_DIALOG_TITLE = StringConstants.HAND_NEW_PROJECT_NAME_TITLE;
	private static final int IMPORT_PROJECT_AS_NEW_PROJECT_MAX_PROGRESS = 17;
	

	@CanExecute
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute(Shell shell) {
		try {
			DirectoryDialog directoryDialog = new DirectoryDialog(shell);
			directoryDialog.open();

			if (directoryDialog.getFilterPath() != null) {
				File importDirectory = new File(directoryDialog.getFilterPath());
				if (importDirectory != null && importDirectory.exists() && importDirectory.isDirectory()) {
					InputDialog projectNameDialog = new InputDialog(shell, PROJECT_NAME_DIALOG_TITLE,
							PROJECT_NAME_DIALOG_MESSAGE, "", null);
					projectNameDialog.open();

					if (projectNameDialog.getValue() != null && !projectNameDialog.getValue().isEmpty()) {
						ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
						progressDialog.run(true, true,
								new com.kms.katalon.composer.components.impl.thread.ImportExportProgressThread(IMPORT_PROJECT_AS_NEW_PROJECT_MAX_PROGRESS,
										new ImportProjectAsNewProjectController(
												UUID.randomUUID().toString(), null, importDirectory.getAbsolutePath(), 
												LoggerSingleton.getInstance().getLogger(), 
												projectNameDialog.getValue())));
					}
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
