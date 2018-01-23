package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.impl.dialogs.TextEncryptionDialog;

public class SecuredTextDialogCellEditor extends AbstractDialogCellEditor {
    
    public SecuredTextDialogCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        TextEncryptionDialog dialog = new TextEncryptionDialog(Display.getCurrent().getActiveShell());
        dialog.setBlockOnOpen(true);
        dialog.open();
        return dialog.getEncryptedText();
    }

}
