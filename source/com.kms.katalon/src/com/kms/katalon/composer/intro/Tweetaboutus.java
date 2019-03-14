package com.kms.katalon.composer.intro;

import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.tracking.service.Trackings;

public class Tweetaboutus extends AbstractHandler {
    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        Program.launch("https://www.katalon.com/love-katalon");
        Trackings.trackQuickDiscussion();
    }
}
