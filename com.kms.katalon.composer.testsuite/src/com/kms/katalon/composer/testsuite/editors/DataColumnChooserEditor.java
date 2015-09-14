package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.testsuite.dialogs.TestDataColumnChooserDialog;

public class DataColumnChooserEditor extends DialogCellEditor {

    private String[] columnNames;
    private String selectionColumnName;

    public DataColumnChooserEditor(Composite parent, String[] columnNames, String selectionColumnName) {
        super(parent);
        setColumnNames(columnNames);
        setSelectionColumnName(selectionColumnName);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        // TODO Auto-generated method stub
        TestDataColumnChooserDialog dialog = new TestDataColumnChooserDialog(cellEditorWindow.getShell(), columnNames,
                selectionColumnName);
        if (dialog.open() == Dialog.OK) {
            return dialog.getSelectedColumnName();
        } else {
            return selectionColumnName;
        }
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public String getSelectionColumnName() {
        return selectionColumnName;
    }

    public void setSelectionColumnName(String selectionColumnName) {
        this.selectionColumnName = selectionColumnName;
    }

}
