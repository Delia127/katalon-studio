package com.kms.katalon.composer.handlers;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.CommandPaletteDialog;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public class CommandPaletteHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        new CommandPaletteDialog(Display.getCurrent().getActiveShell()).open();
//        new TextEncryptionDialog(Display.getCurrent().getActiveShell()).open();
    }

}
