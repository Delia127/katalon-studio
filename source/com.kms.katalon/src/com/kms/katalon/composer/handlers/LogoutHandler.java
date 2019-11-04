package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.application.ApplicationStaupHandler;
import com.kms.katalon.logging.LogUtil;

public class LogoutHandler {

    @Execute
    public void execute() {
        try {
            ActivationInfoCollector.setActivated(false);
            ActivationInfoCollector.clearFeatures();

            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            eventBroker.send(EventConstants.PROJECT_CLOSE, null);

            if (ApplicationStaupHandler.checkActivation(false)) {
                eventBroker.post(EventConstants.ACTIVATION_CHECKED, null);
            }

        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }
}
