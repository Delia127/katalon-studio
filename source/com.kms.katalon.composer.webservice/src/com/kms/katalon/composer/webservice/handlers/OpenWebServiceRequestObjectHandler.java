package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.util.WSRequestPartService;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class OpenWebServiceRequestObjectHandler {

    @Inject
    MApplication application;

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object.getClass() == WebServiceRequestEntity.class) {
                    openRequestObject((WebServiceRequestEntity) object);
                }
            }
        });
    }

    public void openRequestObject(WebServiceRequestEntity requestObject) {
        try {
            WSRequestPartService.openPart(requestObject);
        } catch (IOException | CoreException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.MSG_CANNOT_OPEN_REQUEST);
        }
    }
}
