package com.kms.katalon.composer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import com.kms.katalon.composer.intro.FunctionsIntroductionDialog;
import com.kms.katalon.constants.EventConstants;

public class OpenFunctionsIntroductionHandler {

    @Execute
    public void execute() {
        FunctionsIntroductionDialog dialog = new FunctionsIntroductionDialog(null);
        dialog.open();
    }

    @Inject
    @Optional
    public void execute(@UIEventTopic(EventConstants.KATALON_QUICK_GUIDE) Object eventData) {
        execute();
    }
}
