package com.kms.katalon.tracking.model;

import com.google.gson.JsonObject;

public class TrackInfo {

    private String eventName;
    
    private boolean isAnonymous = false;
    
    private JsonObject eventProperties = new JsonObject();
    
    public static TrackInfo create() {
        return new TrackInfo();
    }
    
    public TrackInfo eventName(String eventName) {
        this.eventName = eventName;
        return this;
    }
    
    public TrackInfo anonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
        return this;
    }
    
    public TrackInfo properties(JsonObject properties) {
        this.eventProperties = properties;
        return this;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public boolean isAnonymous() {
        return isAnonymous;
    }
    
    public JsonObject getProperties() {
        return eventProperties;
    }
}
