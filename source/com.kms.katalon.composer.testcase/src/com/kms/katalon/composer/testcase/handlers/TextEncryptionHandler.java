package com.kms.katalon.composer.testcase.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.dialogs.TextEncryptionDialog;

public class TextEncryptionHandler {

    @Execute
    public void execute() {
        Shell shell = Display.getCurrent().getActiveShell();
        TextEncryptionDialog dialog = TextEncryptionDialog.createDefault(shell);
        dialog.setBlockOnOpen(true);
        dialog.open();
    }
}
