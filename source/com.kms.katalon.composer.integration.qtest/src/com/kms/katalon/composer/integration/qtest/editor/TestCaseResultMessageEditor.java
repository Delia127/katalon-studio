package com.kms.katalon.composer.integration.qtest.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.integration.qtest.dialog.MessageEditingDialog;

public class TestCaseResultMessageEditor extends DialogCellEditor {
    private String message;

    public TestCaseResultMessageEditor(Composite parent, String message) {
        super(parent);
        setMessage(message);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        MessageEditingDialog dialog = new MessageEditingDialog(cellEditorWindow.getShell(), message);
        if (dialog.open() == Dialog.OK) {
            message = dialog.getMessage();
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
