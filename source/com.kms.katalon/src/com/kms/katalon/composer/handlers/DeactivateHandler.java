package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.MachineUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.application.ApplicationStaupHandler;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class DeactivateHandler {
    @Execute
    public void execute() {
        try {
            ActivationInfoCollector.setActivated(false);
            ActivationInfoCollector.clearFeatures();

            String username = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
            String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);
            String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
            String machineId = MachineUtil.getMachineId();
            ActivationInfoCollector.deactivate(username, password, machineId);

            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            if (ApplicationStaupHandler.checkActivation(false)) {
                eventBroker.post(EventConstants.ACTIVATION_CHECKED, null);
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }
}
