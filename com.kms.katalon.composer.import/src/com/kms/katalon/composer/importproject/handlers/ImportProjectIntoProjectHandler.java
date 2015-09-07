package com.kms.katalon.composer.importproject.handlers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.UUID;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.thread.ImportExportProgressThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.importproject.components.DuplicateEntityDialog;
import com.kms.katalon.controller.ImportProjectIntoProjectController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.util.ImportDuplicateEntityParameter;
import com.kms.katalon.entity.util.ImportDuplicateEntityResult;
import com.kms.katalon.entity.util.ImportType;

public class ImportProjectIntoProjectHandler implements PropertyChangeListener {
	private static final int IMPORT_PROJECT_INTO_PROJECT_MAX_PROGRESS = 17;
	private final static String DUPLICATE_ENTITY_EVENT = "duplicateentity";

	private ImportProjectIntoProjectController importProjectController;

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
					File importDirectory = new File(directoryDialog.getFilterPath());
					if (importDirectory != null && importDirectory.exists() && importDirectory.isDirectory()) {
						importProjectController = new ImportProjectIntoProjectController(UUID.randomUUID().toString(),
								ProjectController.getInstance().getCurrentProject(), importDirectory.getAbsolutePath(),
								LoggerSingleton.getInstance().getLogger(), this);
						ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
						progressDialog.run(true, true, new ImportExportProgressThread(
								IMPORT_PROJECT_INTO_PROJECT_MAX_PROGRESS, importProjectController));
					}
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private ImportType getImportTypeResult(DuplicateEntityDialog dialog) {
		switch (dialog.getReturnCode()) {
		case DuplicateEntityDialog.CREAT_NEW_BUTTON_ID:
			return ImportType.New;
		case DuplicateEntityDialog.OVERRIDE_BUTTON_ID:
			return ImportType.Override;
		case DuplicateEntityDialog.MERGE_BUTTON_ID:
			return ImportType.Merge;
		case IDialogConstants.CANCEL_ID:
			return ImportType.Cancel;
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if (propertyChangeEvent.getPropertyName().equals(DUPLICATE_ENTITY_EVENT)) {
			try {
				final Object object = propertyChangeEvent.getNewValue();
				if (object != null && object instanceof ImportDuplicateEntityParameter) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								DuplicateEntityDialog dialog = new DuplicateEntityDialog(null,
										(ImportDuplicateEntityParameter) object);
								dialog.open();
								importProjectController.setImportDuplicateEntityResult(new ImportDuplicateEntityResult(
										getImportTypeResult(dialog), dialog.getIsApplyToAll()));
							} catch (Exception e) {
								LoggerSingleton.logError(e);
							}
						}
					});

				}
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
			
		}
	}
}
