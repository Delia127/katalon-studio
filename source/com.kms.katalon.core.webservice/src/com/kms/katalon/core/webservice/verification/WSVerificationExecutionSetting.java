package com.kms.katalon.core.webservice.verification;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.testobject.ResponseObject;

public class WSVerificationExecutionSetting {
    
    private static final String REQUEST_OBJECT_ID = "requestObjectId";
    
    private static final String RESPONSE_OBJECT = "responseObject";
    
    private static final ThreadLocal<Map<String, Object>> localExecutionSettingMapStorage = 
        new ThreadLocal<Map<String, Object>>() {
            @Override
            protected Map<String, Object> initialValue() {
                return new HashMap<String, Object>();
            }
        };

    public static String getRequestObjectId() {
        return (String) localExecutionSettingMapStorage.get().get(REQUEST_OBJECT_ID);
    }

    public static void setRequestObjectId(String requestObjectId) {
        localExecutionSettingMapStorage.get().put(REQUEST_OBJECT_ID, requestObjectId);
    }

    public static ResponseObject getResponseObject() {
        return (ResponseObject) localExecutionSettingMapStorage.get().get(RESPONSE_OBJECT);
    }

    public static void setResponseObject(ResponseObject responseObject) {
        localExecutionSettingMapStorage.get().put(RESPONSE_OBJECT, responseObject);
    }
}
