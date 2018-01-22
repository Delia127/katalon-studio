package com.kms.katalon.composer.components.impl.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.TextEncryptionDialog;

public class TextEncryptionHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        TextEncryptionDialog dialog = new TextEncryptionDialog(activeShell);
        dialog.open();
    }
}
