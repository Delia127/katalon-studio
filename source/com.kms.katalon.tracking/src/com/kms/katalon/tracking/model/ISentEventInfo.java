package com.kms.katalon.tracking.model;

import com.google.gson.JsonObject;

public interface ISentEventInfo {

    String getEventName();
    
    boolean isAnonymous();
    
    JsonObject getPropertiesObject();
}
