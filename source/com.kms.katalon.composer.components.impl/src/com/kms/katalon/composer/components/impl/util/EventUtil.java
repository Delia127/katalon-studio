package com.kms.katalon.composer.components.impl.util;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;

public class EventUtil {

    public static MPart getPart(Event event) {
        Object element = event.getProperty(EventTags.ELEMENT);

        if (!(element instanceof MPart)) {
            return null;
        }

        return (MPart) element;
    }

    public static Object getData(Event event) {
        return event.getProperty(IEventBroker.DATA);
    }

    public static void post(String eventTopic, Object data) {
        EventBrokerSingleton.getInstance().getEventBroker().post(eventTopic, data);
    }

    public static void send(String eventTopic, Object data) {
        EventBrokerSingleton.getInstance().getEventBroker().send(eventTopic, data);
    }

}
