package com.kms.katalon.composer.handlers;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.about.dialog.KatalonAboutDialog;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public class AboutHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        Shell activeShell = getActiveWorkbenchWindow().getShell();
        new KatalonAboutDialog(activeShell).open();
    }
}
