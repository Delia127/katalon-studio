package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.impl.editors.CustomDialogCellEditor;
import com.kms.katalon.composer.testsuite.dialogs.DataIterationDialog;
import com.kms.katalon.entity.link.IterationEntity;

public class DataIterationCellEditor extends CustomDialogCellEditor {

    private IterationEntity iterationEntity;

    public DataIterationCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        DataIterationDialog dialog = new DataIterationDialog(cellEditorWindow.getShell(), iterationEntity.clone());
        if (dialog.open() == Window.OK) {
            return dialog.getIterationEntity();
        }
        return null;
    }

    protected void updateContents(Object value) {
        if (value instanceof IterationEntity) {
            iterationEntity = (IterationEntity) value;
            defaultLabel.setText(iterationEntity.getDisplayString());
        }
    }
}
