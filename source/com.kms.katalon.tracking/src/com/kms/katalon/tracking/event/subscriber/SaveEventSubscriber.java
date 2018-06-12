package com.kms.katalon.tracking.event.subscriber;

import com.google.gson.JsonObject;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.model.ISentEventInfo;

public class SaveEventSubscriber extends AbstractTrackingEventSubscriber {

    @Override
    protected boolean accept(TrackingEvent event) {
        return event.getTrigger() == UsageActionTrigger.SAVE
                || event.getTrigger() == UsageActionTrigger.SAVE_ALL;
    }
    
    @Override
    protected ISentEventInfo getSentEventInfo() {
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
                properties.addProperty("action", trackingEvent.getTrigger().getAction());
                return properties;
            }
            
        };
    }

}
