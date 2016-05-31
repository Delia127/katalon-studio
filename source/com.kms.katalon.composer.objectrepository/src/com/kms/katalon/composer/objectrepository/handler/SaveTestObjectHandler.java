package com.kms.katalon.composer.objectrepository.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;

public class SaveTestObjectHandler {
	@Inject
    IEventBroker eventBroker;

    @PostConstruct
    public void registerEventHandler() {
    }

    @Inject
    @Optional
    private void getNotificationsFromTestObject(@UIEventTopic(EventConstants.TEST_OBJECT_SAVE) WebElementEntity entity) {
        if (entity != null) {
            try {
            	ObjectRepositoryController.getInstance().updateTestObject(entity);
                eventBroker.post(EventConstants.EXPLORER_REFRESH, entity);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, 
                		StringConstants.HAND_ERROR_MSG_UNABLE_TO_SAVE_TEST_OBJ);
            }
        }
    }
}
