package com.kms.katalon.composer.KatalonQuickStart;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public class QuickStartDialogHandle extends AbstractHandler {

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
