package com.kms.katalon.composer.webservice.handlers;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.internal.workbench.PartServiceImpl;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.WSRequestPartUI;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenWebServiceRequestObjectHandler {

    @Inject
    MApplication application;

    @Inject
    EModelService modelService;
    
    @Inject
    IEclipseContext context;

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

    @Inject
    @Optional
    private void getNotifications(
            @UIEventTopic(EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN) WebServiceRequestEntity entity) {
        openRequestObject(entity);
    }
    
    public void openRequestObject(WebServiceRequestEntity requestObject) {
        try {
            EPartService partService = getPartService();
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            String partId = EntityPartUtil.getTestObjectPartId(requestObject.getId());
            MPart mPart = (MPart) modelService.find(partId, application);
            if (stack != null) {
                if (mPart == null) {
                    WSRequestPartUI.create(requestObject, stack);
                } else {
                    stack.setSelectedElement(mPart);
                }
                Trackings.trackOpenObject("webServiceRequest");
            }
        } catch (IOException | CoreException e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.MSG_CANNOT_OPEN_REQUEST);
        }
    }
    
    private EPartService getPartService() {
        EPartService partService = (EPartService) context.getActive(PartServiceImpl.class);
        if (partService == null) {
            partService = context.getActive(EPartService.class);
        }
        return partService;
    }
}
