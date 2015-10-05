package com.kms.katalon.composer.execution.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.execution.launcher.manager.LauncherManager;

public class StopExecutingScriptHandler {

    @Inject
    IEventBroker eventBroker;

    @CanExecute
    public boolean canExecute() {
        try {
            return LauncherManager.getInstance().isAnyLauncherRunning();
        } catch (Exception e) {
            return false;
        }

    }

    @Execute
    public void execute() {
        try {
            LauncherManager.getInstance().stopAllLauncher();
            eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
