package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.application.ApplicationStaupHandler;
import com.kms.katalon.logging.LogUtil;

public class LogoutHandler {
        
    @Execute
    public void execute() {
        try {
            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

            ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME, "", true);
            ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, "", true);
            ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_PASSWORD, "", true);
            
            if (ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE) != null) {
                ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_ACTIVATION_CODE, "", true);
            }
            
            if (ApplicationInfo.getAppProperty(ApplicationStringConstants.STORE_TOKEN) != null) {
                ApplicationInfo.setAppProperty(ApplicationStringConstants.STORE_TOKEN, "", true);
            }
            
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);
            
            if (ApplicationStaupHandler.checkActivation()) {
                eventBroker.post(EventConstants.ACTIVATION_CHECKED, null);
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }
}
