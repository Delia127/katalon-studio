package com.kms.katalon.composer.toolbar;

import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.tracking.service.Trackings;

public class ForumSearchToolControl extends AbstractHandler {
    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        Program.launch("https://forum.katalon.com");
        Trackings.trackQuickDiscussion();
    }
}