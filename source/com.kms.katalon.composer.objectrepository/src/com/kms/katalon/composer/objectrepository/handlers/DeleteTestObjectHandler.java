package com.kms.katalon.composer.objectrepository.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.objectrepository.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.dal.exception.EntityIsReferencedException;
import com.kms.katalon.entity.repository.WebElementEntity;

public class DeleteTestObjectHandler {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof WebElementTreeEntity) {
                    excute((WebElementTreeEntity) object);
                }
            }
        });
    }

    private void excute(WebElementTreeEntity webElementTreeEntity) {
        try {
            WebElementEntity webElement = (WebElementEntity) webElementTreeEntity.getObject();

            // remove TestCase part from its partStack if it exists
            EntityPartUtil.closePart(webElement);

            ObjectRepositoryController.getInstance().deleteWebElement(webElement);

            eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, ObjectRepositoryController.getInstance()
                    .getIdForDisplay(webElement));
        } catch (EntityIsReferencedException e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, e.getMessage());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_OBJ);
        }
    }

}
