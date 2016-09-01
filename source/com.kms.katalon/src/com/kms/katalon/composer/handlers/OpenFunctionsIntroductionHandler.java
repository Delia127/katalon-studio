package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.composer.intro.FunctionsIntroductionDialog;

public class OpenFunctionsIntroductionHandler {

    @Execute
    public void execute() {
        FunctionsIntroductionDialog dialog = new FunctionsIntroductionDialog(null);
        dialog.open();
    }
}
