package com.kms.katalon.composer.execution;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileManager {

    @Inject
    private IEventBroker eventBroker;

    private static ExecutionProfileManager instance;

    private ExecutionProfileEntity selectedProfile;

    @PostConstruct
    private void initEventListeners() {
        instance = this;
        eventBroker.subscribe(EventConstants.PROFILE_SELECTED_PROIFE_CHANGED, new EventServiceAdapter() {

            @Override
            public void handleEvent(Event event) {
                if (EventConstants.PROFILE_SELECTED_PROIFE_CHANGED.equals(event.getTopic())) {
                    selectedProfile = (ExecutionProfileEntity) getObject(event);
                }
            }

        });
    }

    public ExecutionProfileEntity getSelectedProfile() {
        return selectedProfile;
    }

    public static ExecutionProfileManager getInstance() {
        return instance;
    }
}
