package com.kms.katalon.composer.global.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class PasteExecutionProfileHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private void initEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_PASTE_SELECTED_ITEM, new EventServiceAdapter() {
            
            @Override
            public void handleEvent(Event event) {
                // TODO Auto-generated method stub
                Object item = getObject(event);
                if (!(item instanceof ExecutionProfileEntity)) {
                    return;
                }
                ExecutionProfileEntity profile = (ExecutionProfileEntity) item;
            }
        });
    }
}
