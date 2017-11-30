
package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.constants.MessageConstants;

public class ReportIssueHandler extends AbstractHandler {
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        Program.launch(MessageConstants.URL_KATALON_DISCUSSION_FORUM);
    }
}
