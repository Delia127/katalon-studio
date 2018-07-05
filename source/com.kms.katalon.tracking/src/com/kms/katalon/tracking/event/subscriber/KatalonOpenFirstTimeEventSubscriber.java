package com.kms.katalon.tracking.event.subscriber;

import com.google.gson.JsonObject;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ISentEventInfo;

public class KatalonOpenFirstTimeEventSubscriber extends AbstractTrackingEventSubscriber {
    
    @Override
    protected boolean accept(TrackingEvent event) {
        return event.getTrigger() == UsageActionTrigger.OPEN_FIRST_TIME;
    }

    @Override
    protected ISentEventInfo getSentEventInfo() {
        return new ISentEventInfo() {
            
            @Override
            public boolean isAnonymous() {
                return true;
            }
            
            @Override
            public JsonObject getPropertiesObject() {
                JsonObject properties = new JsonObject();
                properties.addProperty("triggeredBy", trackingEvent.getTrigger().getAction());
                return properties;
            }
            
            @Override
            public String getEventName() {
                return TrackEvents.KATALON_OPEN_FIRST_TIME;
            }
        };
    }
}
