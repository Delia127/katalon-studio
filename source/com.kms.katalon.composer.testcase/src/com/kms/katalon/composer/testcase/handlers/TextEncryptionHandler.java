package com.kms.katalon.composer.testcase.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.testcase.dialogs.TextEncryptionDialog;
import com.kms.katalon.controller.ProjectController;

public class TextEncryptionHandler {

    @Execute
    public void execute() {
        TextEncryptionDialog dialog = new TextEncryptionDialog(Display.getCurrent().getActiveShell());
        dialog.setBlockOnOpen(true);
        dialog.open();
    }
    
    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }
}
