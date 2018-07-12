package com.kms.katalon.integration.qtest.handler;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.window.Window;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.integration.qtest.activation.dialog.QTestActivationDialog;

public class QTestIntegrationActivationCheckHandler {
    
    @Inject
    private IEventBroker eventBroker;
    
    @PostConstruct
    public void registerQTestActivationCheckEvent() {
        eventBroker.subscribe(EventConstants.ACTIVATION_QTEST_INTEGRATION_CHECK, event -> {
            openQtestActivationDialogCheck();
        });
    }
    
    private void openQtestActivationDialogCheck() {
        boolean activated = true;
        int result = new QTestActivationDialog(null).open();
        if (result == Window.CANCEL) {
            activated = false;
        }

        eventBroker.send(EventConstants.ACTIVATION_QTEST_INTEGRATION_CHECK_COMPLETED, activated);
    }

}
