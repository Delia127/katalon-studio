package com.kms.katalon.tracking.event.subscriber;

import java.util.Map;

import com.google.gson.JsonObject;
import com.kms.katalon.application.RunningMode;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ISentEventInfo;

public class TestSuiteExecutionEventSubscriber extends AbstractTrackingEventSubscriber {

    @Override
    protected boolean accept(TrackingEvent event) {
        return event.getTrigger() == UsageActionTrigger.EXECUTE_TEST_SUITE;
    }
    
    @Override
    protected ISentEventInfo getSentEventInfo() {
        Map<String, Object> properties = (Map<String, Object>) trackingEvent.getData();
        boolean isAnonymous = (boolean) properties.get("isAnonymous");
        String runningMode = (String) properties.get("runningMode");
        String launchMode = (String) properties.get("launchMode");
        
        return new ISentEventInfo() {

            @Override
            public String getEventName() {
                return TrackEvents.KATALON_STUDIO_USED;
            }

            @Override
            public boolean isAnonymous() {
                return isAnonymous;
            }

            @Override
            public JsonObject getPropertiesObject() {
                JsonObject properties = new JsonObject();
                properties.addProperty("action", "execute");
                properties.addProperty("object", "Test Suite");
                properties.addProperty("runningMode", runningMode);
                if (RunningMode.GUI.getMode().equals(runningMode)) {
                    properties.addProperty("launchMode", launchMode);
                }
                return properties;
            }
            
        };
    }

}
