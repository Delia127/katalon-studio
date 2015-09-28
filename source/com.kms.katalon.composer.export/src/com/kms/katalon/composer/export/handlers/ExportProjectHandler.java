package com.kms.katalon.composer.export.handlers;

import java.io.File;
import java.util.UUID;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.thread.ImportExportProgressThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ExportProjectController;
import com.kms.katalon.controller.ProjectController;

public class ExportProjectHandler {
	private static final int EXPORT_PROJECT_MAX_PROGRESS = 4;

	@CanExecute
	public boolean canExecute() {
		if (ProjectController.getInstance().getCurrentProject() != null) {
			return true;
		}
		return false;
	}

	@Execute
	public void execute(Shell shell) {
		try {
			if (ProjectController.getInstance().getCurrentProject() != null) {
				DirectoryDialog directoryDialog = new DirectoryDialog(shell);
				directoryDialog.open();

				if (directoryDialog.getFilterPath() != null) {
					File exportDirectory = new File(directoryDialog.getFilterPath());
					if (exportDirectory != null && exportDirectory.exists() && exportDirectory.isDirectory()) {
						ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
						progressDialog.run(true, true,
								new ImportExportProgressThread( EXPORT_PROJECT_MAX_PROGRESS, 
										new ExportProjectController(UUID.randomUUID().toString(), 
												ProjectController.getInstance().getCurrentProject(),
												exportDirectory.getAbsolutePath())));
					}
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
