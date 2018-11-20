package com.kms.katalon.platform;

public interface EventService {
    void fireEvent(String eventName, Object eventObject);
}
