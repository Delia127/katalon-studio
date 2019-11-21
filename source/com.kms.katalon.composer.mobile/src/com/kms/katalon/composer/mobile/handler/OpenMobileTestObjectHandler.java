package com.kms.katalon.composer.mobile.handler;

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
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.composer.mobile.part.MobileTestObjectPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.repository.MobileElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenMobileTestObjectHandler {
    private static final String MOBILE_TEST_OBJECT_PART_URI = "bundleclass://com.kms.katalon.composer.mobile/"
            + MobileTestObjectPart.class.getName();
    
    @Inject
    MApplication application;

    @Inject
    EPartService partService;
    
    @Inject
    EModelService modelService;

    private static OpenMobileTestObjectHandler instance;
    
    //@Inject
    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        instance = this;
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object.getClass() == MobileElementEntity.class) {
                    execute((MobileElementEntity) object);
                }
            }
        });
    }
    
    @Inject
    @Optional
    private void getNotifications(@UIEventTopic(EventConstants.MOBILE_TEST_OBJECT_OPEN) MobileElementEntity entity){
        execute(entity);
    }
    
    public void execute(MobileElementEntity testObject) {
        if (testObject != null) {
            String partId = EntityPartUtil.getMobileTestObjectPartId(testObject.getId());
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            MPart mPart = (MPart) modelService.find(partId, application);
            boolean alreadyOpened = true;
            if (mPart == null) {
                mPart = modelService.createModelElement(MPart.class);
                mPart.setElementId(partId);
                mPart.setLabel(testObject.getName());
                mPart.setContributionURI(MOBILE_TEST_OBJECT_PART_URI);
                mPart.setCloseable(true);
                mPart.setIconURI(ImageConstants.IMG_URL_16_MOBILE);
                mPart.setTooltip(testObject.getIdForDisplay());
                mPart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
                stack.getChildren().add(mPart);
                alreadyOpened = false;
            }
            
            if (mPart.getObject() == null) {
                mPart.setObject(testObject);
            }
            partService.showPart(mPart, PartState.ACTIVATE);
            partService.bringToTop(mPart);
            stack.setSelectedElement(mPart);
            
            if (!alreadyOpened) {
                Trackings.trackOpenObject("mobileTestObject");
            }
        }
    }

    public static OpenMobileTestObjectHandler getInstance() {
        return instance;
    }
}
