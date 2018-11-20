package com.kms.katalon.platform;

import org.eclipse.e4.core.services.events.IEventBroker;

public class EventServiceImpl implements EventService {

    @Override
    public void fireEvent(String eventName, Object eventObject) {
        IEventBroker eventBroker = ApplicationServiceImpl.get(IEventBroker.class);
        eventBroker.post(eventName, eventObject);
    }

}
