package com.kms.katalon.integration.qtest.handler;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.window.Window;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.integration.qtest.activation.dialog.QTestActivationDialog;
import com.kms.katalon.integration.qtest.helper.QTestActivationHelper;

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
        String activedPropName = ApplicationInfo.getAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME);
        boolean activatedBefore = !(activedPropName == null || activedPropName.isEmpty());
        int result;
        boolean activated = true;
        
        if (activatedBefore) {
            String usedActivationCode = ApplicationInfo.getAppProperty(ApplicationStringConstants.ACTIVATION_CODE);
            StringBuilder errorMessage = new StringBuilder();
            boolean activationStillValid = QTestActivationHelper.qTestactivate(usedActivationCode, errorMessage);
            if (activationStillValid) {
                eventBroker.send(EventConstants.ACTIVATION_QTEST_INTEGRATION_CHECK_COMPLETED, activationStillValid);
                return;
            }
            result = new QTestActivationDialog(null, true).open();
        } else {
            result = new QTestActivationDialog(null, false).open();
        }
        
        if (result == Window.CANCEL) {
            activated = false;
        }
        eventBroker.send(EventConstants.ACTIVATION_QTEST_INTEGRATION_CHECK_COMPLETED, activated);
    }

}
