package com.kms.katalon.composer.handlers;

import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.tracking.service.Trackings;

public class ChatSupportHandler extends AbstractHandler {
    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        Program.launch("https://gitter.im/katalon-studio/QuickSupport");
        Trackings.trackQuickDiscussion();
    }

}
