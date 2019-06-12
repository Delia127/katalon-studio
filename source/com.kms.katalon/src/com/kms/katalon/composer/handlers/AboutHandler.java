package com.kms.katalon.composer.handlers;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.about.dialog.KatalonAboutDialog;
import com.kms.katalon.activation.ActivationService;
import com.kms.katalon.activation.ActivationServiceConsumer;
import com.kms.katalon.composer.KatalonQuickStart.QuickStartDialog;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public class AboutHandler extends AbstractHandler {

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        ActivationService activationService = ActivationServiceConsumer.getServiceInstance();
        Shell activeShell = getActiveWorkbenchWindow().getShell();
        if (activationService != null) {
            activationService.openAboutDialog(activeShell);
        } else {
            new KatalonAboutDialog(activeShell).open();
        }
    }
}
