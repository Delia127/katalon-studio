package com.kms.katalon.composer.execution.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class CleanAllTerminatedHandler {
    @Inject
    private IEventBroker eventBroker;

    @Execute
    public void execute() {
        LauncherManager.getInstance().removeAllTerminated();
        eventBroker.post(EventConstants.CONSOLE_LOG_RESET, null);
        eventBroker.post(EventConstants.JOB_REFRESH, null);
    }
}
