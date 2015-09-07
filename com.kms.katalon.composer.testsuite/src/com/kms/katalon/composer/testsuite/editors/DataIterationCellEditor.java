package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.testsuite.dialogs.DataIterationDialog;
import com.kms.katalon.entity.link.IterationEntity;

public class DataIterationCellEditor extends DialogCellEditor {
	
	private IterationEntity iterationEntity;
	
	public DataIterationCellEditor(Composite parent, IterationEntity iterationEntity) {
		super(parent);
		this.iterationEntity = iterationEntity;
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		DataIterationDialog dialog = new DataIterationDialog(cellEditorWindow.getShell(), iterationEntity);
		if (dialog.open() == Window.OK) {
			return dialog.getIterationEntity();
		}
		return null;
	}

}
