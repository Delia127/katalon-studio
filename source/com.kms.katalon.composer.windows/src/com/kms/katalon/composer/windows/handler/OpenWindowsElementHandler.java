package com.kms.katalon.composer.windows.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.windows.part.WindowsObjectPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.repository.WindowsElementEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenWindowsElementHandler {
    
    private static final String WINDOWS_OBJECT_PART_URI = "bundleclass://com.kms.katalon.composer.windows/" + WindowsObjectPart.class.getName();

    private static OpenWindowsElementHandler instance;
    
    @Inject
    private MApplication application;

    @Inject
    private static EModelService modelService;

    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private EPartService partService;

    @PostConstruct
    public void onCreated() {
        instance = this;
    }

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof WindowsElementEntity) {
                    openWindowsElement((WindowsElementEntity) object);
                }
            }
        });
    }

    public static OpenWindowsElementHandler getInstance() {
        return instance;
    }

    public void openWindowsElement(WindowsElementEntity windowsElementEntity) {
        try {
            if (windowsElementEntity != null) {
                String partId = EntityPartUtil.getWindowsTestObjectPartId(windowsElementEntity.getId());
                MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                        application);
                MPart mPart = (MPart) modelService.find(partId, application);
                boolean alreadyOpened = true;
                if (mPart == null) {
                    mPart = modelService.createModelElement(MPart.class);
                    mPart.setElementId(partId);
                    mPart.setLabel(windowsElementEntity.getName());

                    mPart.setContributionURI(WINDOWS_OBJECT_PART_URI);
                    mPart.setIconURI(ImageManager.getImageURLString(IImageKeys.WINDOWS_ENTITY_16));
                    mPart.setCloseable(true);
                    mPart.setTooltip(windowsElementEntity.getIdForDisplay());
                    stack.getChildren().add(mPart);
                    alreadyOpened = false;
                }

                if (mPart.getObject() == null) {
                    mPart.setObject(windowsElementEntity);
                }

                partService.showPart(mPart, PartState.ACTIVATE);
                stack.setSelectedElement(mPart);
                
                if (!alreadyOpened) {
                    Trackings.trackOpenObject("testData");
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
