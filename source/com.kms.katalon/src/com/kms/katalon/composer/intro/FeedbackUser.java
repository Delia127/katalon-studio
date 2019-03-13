package com.kms.katalon.composer.intro;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.composer.intro.InAppSurveyDialog;
public class FeedbackUser extends AbstractHandler{
    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
       InAppSurveyDialog feedback = new InAppSurveyDialog(Display.getCurrent().getActiveShell());
       feedback.open();
       // Program.launch("feedback");
        Trackings.trackQuickDiscussion();
    }
}
