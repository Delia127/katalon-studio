package com.kms.katalon.composer.handlers;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.quickstart.QuickStartDialog;

public class OpenQuickStartDialogHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        QuickStartDialog dialog = new QuickStartDialog(Display.getCurrent().getActiveShell());
        dialog.open();

    }
}
