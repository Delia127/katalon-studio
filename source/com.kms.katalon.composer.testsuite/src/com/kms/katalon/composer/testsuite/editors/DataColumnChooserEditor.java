package com.kms.katalon.composer.testsuite.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.testsuite.dialogs.TestDataColumnChooserDialog;
import com.kms.katalon.core.testdata.TestData;

public class DataColumnChooserEditor extends DialogCellEditor {

    private TestData testData;
    private String selectionColumnName;

    public DataColumnChooserEditor(Composite parent, TestData testData, String selectionColumnName) {
        super(parent);
        setTestData(testData);
        setSelectionColumnName(selectionColumnName);
    }

    private void setTestData(TestData testData) {
        // TODO Auto-generated method stub
        this.testData = testData;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        // TODO Auto-generated method stub
        TestDataColumnChooserDialog dialog = new TestDataColumnChooserDialog(cellEditorWindow.getShell(), testData,
                selectionColumnName);
        if (dialog.open() == Dialog.OK) {
            return dialog.getSelectedColumnName();
        } else {
            return selectionColumnName;
        }
    }

    public String getSelectionColumnName() {
        return selectionColumnName;
    }

    public void setSelectionColumnName(String selectionColumnName) {
        this.selectionColumnName = selectionColumnName;
    }

}
