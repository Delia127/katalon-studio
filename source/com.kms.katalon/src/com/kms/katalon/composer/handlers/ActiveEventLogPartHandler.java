package com.kms.katalon.composer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class ActiveEventLogPartHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private MApplication application;
    
    @Inject
    private EModelService modelService;
    
    @Inject
    private EPartService partService;

    @PostConstruct
    public void registerWorkbenchCreated() {
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                activeEventLogPart();
            }
        });
    }

    private void activeEventLogPart() {
        MUIElement eventLogElement = modelService.find(IdConstants.EVENT_LOG_PART_ID, application);
        if (!(eventLogElement instanceof MPlaceholder)) {
            LoggerSingleton.logWarn("System could not initialize EventLog Part");
            return;
        }
        MPlaceholder eventLog = (MPlaceholder) eventLogElement;
        partService.activate((MPart) eventLog.getRef(), false);
    }
}
