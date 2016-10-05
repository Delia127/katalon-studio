package com.kms.katalon.composer.handlers;

import com.kms.katalon.about.dialog.KatalonAboutDialog;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public class AboutHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        new KatalonAboutDialog(getActiveWorkbenchWindow().getShell()).open();
    }
}
