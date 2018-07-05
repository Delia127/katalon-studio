package com.kms.katalon.application.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.greenrobot.eventbus.EventBus;

import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.constants.UsagePropertyConstant;
import com.kms.katalon.core.event.EventBusSingleton;
import com.kms.katalon.logging.LogUtil;

public class EntityTrackingHelper {
    public static void trackProjectCreated() {
        trackEntityCreated(UsagePropertyConstant.KEY_NUM_PROJECT_CREATED);
    }
    
    public static void trackTestCaseCreated() {
        trackEntityCreated(UsagePropertyConstant.KEY_NUM_TEST_CASE_CREATED);
    }

    private static void trackEntityCreated(String key) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                int previousNumOfEntityCreated = 0;
                try {
                    previousNumOfEntityCreated = Integer.parseInt(ApplicationInfo.getAppProperty(key));
                } catch (NumberFormatException e) {
                    LogUtil.logError(e);
                }
                ApplicationInfo.setAppProperty(key, String.valueOf(previousNumOfEntityCreated + 1), true);
            }
        });
    }
    
    public static void trackCreatingEntity(String type) {
        trackCreatingEntity(type, null);
    }
    
    public static void trackCreatingEntity(String type, String... properties) {
        Map<String, Object> eventData = new HashMap<String, Object>() {{
            put("type", type);
        }};
        
        if (properties != null) {
            for (int i = 0; i < properties.length - 1; i += 2) {
                String propertyKey = properties[i];
                String propertyValue = properties[i + 1];
                eventData.put(propertyKey, propertyValue);
            }
        }
        
        EventBus eventBus = EventBusSingleton.getInstance().getEventBus();
        eventBus.post(new TrackingEvent(UsageActionTrigger.NEW_OBJECT, eventData));
    }
}
