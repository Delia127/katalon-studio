package com.kms.katalon.composer.integration.qtest.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

import com.katalon.platform.api.Plugin;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.integration.qtest.constant.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class QTestPluginEventHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @PostConstruct
    private void registerEventHandler() {
        eventBroker.subscribe(EventConstants.AFTER_PLUGIN_ACTIVATION, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Plugin plugin = (Plugin) EventUtil.getData(event);
                if (plugin.getPluginId().equals(IdConstants.QTEST_PLUGIN_ID)) {
                    eventBroker.post(EventConstants.EXPLORER_REFRESH, null); //refresh to display qTest overlay icon
                }
            }
        });
        
        eventBroker.subscribe(EventConstants.BEFORE_PLUGIN_DEACTIVATION, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Plugin plugin = (Plugin) EventUtil.getData(event);
                if (plugin.getPluginId().equals(IdConstants.QTEST_PLUGIN_ID)) {
                    eventBroker.post(EventConstants.EXPLORER_REFRESH, null); //refresh to remove qTest overlay icon
                }
            }
        });
    }
}
