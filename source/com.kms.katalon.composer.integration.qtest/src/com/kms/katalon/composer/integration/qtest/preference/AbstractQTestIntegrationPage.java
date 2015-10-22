package com.kms.katalon.composer.integration.qtest.preference;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.preference.PreferencePage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.integration.qtest.constant.EventConstants;

public abstract class AbstractQTestIntegrationPage extends PreferencePage implements EventHandler {

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void init() {
        eventBroker.subscribe(EventConstants.SETUP_FINISHED, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (EventConstants.SETUP_FINISHED.equals(event.getTopic())) {
            initialize();
        }
    }
    
    protected abstract void initialize();
}
