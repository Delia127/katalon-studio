package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testsuite.dialogs.DataColumnFinderDialog;
import com.kms.katalon.composer.testsuite.util.ArrayUtils;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;

public class VariableValueCellEditor extends DialogCellEditor {

	public VariableValueCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		try {
			EntityProvider entityProvider = new EntityProvider();

			FolderEntity rootFolder = FolderController.getInstance().getTestDataRoot(
					ProjectController.getInstance().getCurrentProject());
			String value = (String) getValue();
			String[] parsedStrings = ArrayUtils.arrayStringToArray(value);
			DataColumnFinderDialog dialog = new DataColumnFinderDialog(cellEditorWindow.getShell(),
					new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(entityProvider),
					TreeEntityUtil.getChildren(null, rootFolder), parsedStrings);
			if (dialog.open() == Dialog.OK) {
				return dialog.getReturnValue();
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}

		return null;
	}
}
