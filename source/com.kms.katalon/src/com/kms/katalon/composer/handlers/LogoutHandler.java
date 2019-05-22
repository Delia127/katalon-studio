package com.kms.katalon.composer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.constants.EventConstants;

public class LogoutHandler {
    
    @Inject
    private IEventBroker eventBroker;
    
    @CanExecute
    public boolean canExecute() {
        return true;
    }
    
    @Execute
    public void execute() {
        eventBroker.send(EventConstants.PROJECT_CLOSE, null);

        ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME, "", true);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, "", true);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, "", true);
        
        if (ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE) != null) {
            ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE, "", true);
        }
        
        if (ApplicationInfo.getAppProperty(ApplicationStringConstants.STORE_TOKEN) != null) {
            ApplicationInfo.setAppProperty(ApplicationStringConstants.STORE_TOKEN, "", true);
        }

        eventBroker.send(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, null);
    }
}
