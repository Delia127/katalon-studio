package com.kms.katalon.composer.handlers;

import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.constants.StringConstants;

public class OpenSupportServicePageHandler extends AbstractHandler {
    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        Program.launch(StringConstants.URL_KATALON_SUPPORT_SERVICE);
    }
    

}
