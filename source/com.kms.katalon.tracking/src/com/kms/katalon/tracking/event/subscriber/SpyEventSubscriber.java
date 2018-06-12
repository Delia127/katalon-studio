package com.kms.katalon.tracking.event.subscriber;

import com.google.gson.JsonObject;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ISentEventInfo;

public class SpyEventSubscriber extends AbstractTrackingEventSubscriber {

    @Override
    protected boolean accept(TrackingEvent event) {
        return event.getTrigger() == UsageActionTrigger.SPY;
    }
    
    @Override
    protected ISentEventInfo getSentEventInfo() {
        String target = (String) trackingEvent.getData();
        
        return new ISentEventInfo() {

            @Override
            public String getEventName() {
                return TrackEvents.KATALON_STUDIO_USED;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public JsonObject getPropertiesObject() {
                JsonObject properties = new JsonObject();
                properties.addProperty("action", "spy");
                properties.addProperty("target", target);
                return properties;
            }
            
        };
    }

}
