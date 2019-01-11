package com.kms.katalon.platform.internal.event;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.platform.internal.InternalPlatformService;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;

public class ProjectEventPublisher implements InternalPlatformService {

    private IEventBroker eventBroker;

    private EventHandler eventHandler = new EventHandler() {

        @Override
        public void handleEvent(Event event) {
            switch (event.getTopic()) {
                case EventConstants.PROJECT_OPENED: {
                    eventBroker.post("KATALON_PLUGIN/CURRENT_PROJECT_CHANGED",
                            new ProjectEntityImpl(ProjectController.getInstance().getCurrentProject()));
                }
            }
        }
    };

    public ProjectEventPublisher(IEventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

    @Override
    public void onPostConstruct() {
        if (eventBroker != null) {
            eventBroker.subscribe(EventConstants.PROJECT_OPENED, eventHandler);
        }
    }

    @Override
    public void onPreDestroy() {
        eventBroker.unsubscribe(eventHandler);
    }
}
