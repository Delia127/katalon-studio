package com.kms.katalon.composer.webservice.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.parts.RestServicePart;
import com.kms.katalon.composer.webservice.parts.SoapServicePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class OpenWebServiceRequestObjectHandler {
    public static final String BUNDLE_URI_WEBSERVICE = "bundleclass://com.kms.katalon.composer.webservice/";

    private static final String WEBSERVICE_SOAP_OBJECT_PART_URI = BUNDLE_URI_WEBSERVICE
            + SoapServicePart.class.getName();

    private static final String WEBSERVICE_REST_OBJECT_PART_URI = BUNDLE_URI_WEBSERVICE
            + RestServicePart.class.getName();

    @Inject
    MApplication application;

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    // @Inject
    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object.getClass() == WebServiceRequestEntity.class) {
                    excute((WebServiceRequestEntity) object);
                }
            }
        });
    }

    @Inject
    @Optional
    private void getNotifications(
            @UIEventTopic(EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN) WebServiceRequestEntity entity) {
        excute(entity);
    }

    public void excute(WebServiceRequestEntity requestObject) {
        if (requestObject != null) {
            String partId = EntityPartUtil.getTestObjectPartId(requestObject.getId());
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            MPart mPart = (MPart) modelService.find(partId, application);
            if (mPart == null) {
                mPart = modelService.createModelElement(MPart.class);
                mPart.setElementId(partId);
                mPart.setLabel(requestObject.getName());
                if (WebServiceRequestEntity.SERVICE_TYPES[0].equals(requestObject.getServiceType())) {
                    mPart.setContributionURI(WEBSERVICE_SOAP_OBJECT_PART_URI);
                } else if (WebServiceRequestEntity.SERVICE_TYPES[1].equals(requestObject.getServiceType())) {
                    mPart.setContributionURI(WEBSERVICE_REST_OBJECT_PART_URI);
                }
                mPart.setCloseable(true);
                //TODO we will change this place.
                mPart.setIconURI(ImageConstants.URL_16_WS_TEST_OBJECT);
                mPart.setTooltip(requestObject.getIdForDisplay());
                mPart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
                stack.getChildren().add(mPart);
            }

            if (mPart.getObject() == null) {
                mPart.setObject(requestObject);
            }
            partService.showPart(mPart, PartState.ACTIVATE);
            stack.setSelectedElement(mPart);
        }
    }
}
