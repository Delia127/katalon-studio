package com.kms.katalon.composer.export.handlers;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.thread.ImportExportProgressThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ExportTestCaseController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ExportTestCaseHandler {

	private static final int EXPORT_TEST_CASE_EXTRA_PROGRESS = 3;
	private static final int EXPORT_TEST_CASE_NORMAL_PROGRESS = 8;

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
						List<TestCaseEntity> testCases = FolderController.getInstance().getTestCaseChildren(
								FolderController.getInstance().getTestCaseRoot(
										ProjectController.getInstance().getCurrentProject()));
						if (testCases != null && testCases.size() > 0) {
							int exportTestCaseMaxProgress = testCases.size() * EXPORT_TEST_CASE_NORMAL_PROGRESS
									+ EXPORT_TEST_CASE_EXTRA_PROGRESS;
							ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
							progressDialog.run(true, true, new ImportExportProgressThread(exportTestCaseMaxProgress,
									new ExportTestCaseController(UUID.randomUUID().toString(), 
											ProjectController.getInstance().getCurrentProject(), 
											exportDirectory.getAbsolutePath(), testCases)));
						}
					}
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
}
