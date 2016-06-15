package com.kms.katalon.composer.components.impl.event;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;

public abstract class EventServiceAdapter implements EventHandler {
    protected Object[] getObjects(Event event) {
        Object eventObject = getObject(event);
        if (eventObject != null && eventObject.getClass().isArray()) {
            return (Object[]) eventObject;
        }
        return null;
    }

    protected Object getObject(Event event) {
        return event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
    }
}
