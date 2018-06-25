package com.kms.katalon.tracking.event.subscriber;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.gson.JsonObject;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.constant.TrackEvents;
import com.kms.katalon.tracking.service.TrackingApiService;

public class TrackingEventSubscriber {
    
    public final Map<UsageActionTrigger, Object[]> eventLookup = new HashMap<UsageActionTrigger, Object[]>() {{
        put(UsageActionTrigger.GENERATE_CMD, 
                new Object[] { TrackEvents.KATALON_STUDIO_USED, generateCommandEventDataCollector});
    }};

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void handleEvent(TrackingEvent event) {

        try {
            JsonObject payload = buildEventPayload(event);
            
            sendEventPayload(payload.toString());
        } catch (IOException e) {
            LogUtil.logError(e);
        } catch (GeneralSecurityException e) {
            LogUtil.logError(e);
        }
    }
    
    private JsonObject buildEventPayload(TrackingEvent event) {
        JsonObject payload = new JsonObject();
        
        JsonObject userInfo = userInfoCollector.apply(event);
        JsonUtil.mergeJsonObject(userInfo, payload);
        
        JsonObject eventInfo = eventInfoCollector.apply(event);
        JsonUtil.mergeJsonObject(eventInfo, payload);
        
        Function<TrackingEvent, JsonObject> eventDataCollector = getEventDataCollector(event);
        JsonObject eventData = eventDataCollector.apply(event);
        
        JsonObject applicationTraits = applicationTraitsCollector.apply(event);
        JsonUtil.mergeJsonObject(applicationTraits, eventData);
        
        payload.add("properties", eventData);
        
        return payload;
    }
    
    private void sendEventPayload(String eventPayload) throws IOException, GeneralSecurityException {
        TrackingApiService.getInstance().post(eventPayload);
    }
    
    @SuppressWarnings("unchecked")
    private Function<TrackingEvent, JsonObject> getEventDataCollector(TrackingEvent event) {
        UsageActionTrigger trigger = event.getTrigger();
        return (Function<TrackingEvent, JsonObject>) eventLookup.get(trigger)[1];
    }
    
    private String getSentEventName(TrackingEvent event) {
        UsageActionTrigger trigger = event.getTrigger();
        return (String) eventLookup.get(trigger)[0];
    }
    
    @SuppressWarnings("unchecked")
    private Function<TrackingEvent, JsonObject> userInfoCollector = (event) -> {
        Object eventData = event.getData();
        boolean isAnonymous = false;
        if (eventData != null) {
            Map<String, Object> eventDataMap = (Map<String, Object>) eventData;
            if (eventDataMap.containsKey("isAnonymous")) {
                isAnonymous = (boolean) eventDataMap.get("isAnonymous");
            }
        }
        
        String userId = isAnonymous ? "anonymous" : ApplicationInfo.getAppProperty("email");
        
        JsonObject data = new JsonObject();
        data.addProperty("userId", userId);
        return data;
    };
    
    @SuppressWarnings("unchecked")
    private Function<TrackingEvent, JsonObject> eventInfoCollector = (event) -> {
        UsageActionTrigger trigger = event.getTrigger();
        String eventName = (String) eventLookup.get(trigger)[0];
        
        JsonObject data = new JsonObject();
        data.addProperty("event", eventName);
        return data;
    };
    
    private Function<TrackingEvent, JsonObject> applicationTraitsCollector = (event) -> {
        return ActivationInfoCollector.traitsWithAppInfo();
    };
    
    private Function<TrackingEvent, JsonObject> generateCommandEventDataCollector = (event) -> {
        JsonObject data = new JsonObject();
        data.addProperty("action", "generateCmd");
        return data;
    };
}
