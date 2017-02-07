
package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.zendesk.ZendeskSubmitTicketDialog;

public class ReportIssueHandler extends AbstractHandler {
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        ZendeskSubmitTicketDialog dialog = new ZendeskSubmitTicketDialog(getActiveWorkbenchWindow().getShell());
        dialog.open();
    }
}
