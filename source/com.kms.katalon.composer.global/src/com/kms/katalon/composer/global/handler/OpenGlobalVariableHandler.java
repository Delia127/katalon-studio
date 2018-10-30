package com.kms.katalon.composer.global.handler;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.global.part.ExecutionProfilePartUI;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.tracking.service.Trackings;

public class OpenGlobalVariableHandler {
    @Inject
    MApplication application;

    @Inject
    EModelService modelService;

    @Inject
    IEclipseContext context;
    
    @Inject
    EPartService partService;
    
    @PostConstruct
    public void registerEventHandler(IEventBroker eventBroker) {
        eventBroker.subscribe(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && ExecutionProfileEntity.class.isInstance(object)) {
                	execute((ExecutionProfileEntity) object);
                }
            }
        });
    }

    protected void execute(ExecutionProfileEntity profileEntity) {
    	boolean alreadyOpened = false;
        try {
            MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
            String partId = getPartId(profileEntity);
            MPart mPart = (MPart) modelService.find(partId, application);
            alreadyOpened = mPart != null;
            if (stack != null) {
                if (mPart == null) {
                	ExecutionProfilePartUI.create(profileEntity, stack);
                } else {
                    stack.setSelectedElement(mPart);
                }
            }
        } catch (IOException | CoreException e) {
            LoggerSingleton.logError(e);
        }
        
        if (!alreadyOpened) {
            Trackings.trackOpenObject("profile");
        }
    }

    public String getPartId(ExecutionProfileEntity executionProfile) {
        return EntityPartUtil.getExecutionProfilePartId(executionProfile.getIdForDisplay());
    }

}
