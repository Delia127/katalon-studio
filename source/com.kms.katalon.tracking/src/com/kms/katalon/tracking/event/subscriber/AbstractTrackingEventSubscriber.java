package com.kms.katalon.tracking.event.subscriber;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.model.ISentEventInfo;
import com.kms.katalon.tracking.service.TrackingApiService;

public abstract class AbstractTrackingEventSubscriber {

    protected TrackingEvent trackingEvent;
    
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void handleEvent(TrackingEvent event) {
        if (accept(event)) {
            trackingEvent = event;
            doHandleEvent(event);
        }
    }
    
    protected  boolean accept(TrackingEvent event) {
        return true;
    }
    
    protected void doHandleEvent(TrackingEvent event) {
        try {
            ISentEventInfo sentEventInfo = getSentEventInfo();
            
            String payload = buildEventPayload(sentEventInfo);
            
            sendEventPayload(payload);
        } catch (IOException e) {
            LogUtil.logError(e);
        } catch (GeneralSecurityException e) {
            LogUtil.logError(e);
        }
    }

    protected abstract ISentEventInfo getSentEventInfo();
    
    protected String buildEventPayload(ISentEventInfo eventInfo) {
        PayloadBuilder payloadBuilder = new PayloadBuilder();
        String payloadJson = payloadBuilder.build(eventInfo);
        return payloadJson;
    }

    protected void sendEventPayload(String eventPayload) throws IOException, GeneralSecurityException {
        TrackingApiService.getInstance().post(eventPayload);
    }

    private class PayloadBuilder {
        
        public String build(ISentEventInfo sentEventInfo) {
            JsonObject payload = new JsonObject();
            
            payload = addUserInfo(payload, sentEventInfo);
            
            payload = addEventName(payload, sentEventInfo);
            
            payload = addProperties(payload, sentEventInfo);
            
            return payload.toString();
        }
        
        private JsonObject addUserInfo(JsonObject payload, ISentEventInfo eventInfo) {
            String userId = getUserId(eventInfo.isAnonymous());
            payload.addProperty("userId", userId);
            return payload;
        }
        
        private String getUserId(boolean isAnonymous) {
            return isAnonymous ? "anonymous" : ApplicationInfo.getAppProperty("email");
        }
        
        private JsonObject addEventName(JsonObject payload, ISentEventInfo eventInfo) {
            String eventName = eventInfo.getEventName();
            payload.addProperty("event", eventName);
            return payload;
        }
        
        private JsonObject addProperties(JsonObject payload, ISentEventInfo eventInfo) {
            JsonObject properties = eventInfo.getPropertiesObject();
            
            properties = addApplicationTraits(properties);
            
            payload.add("properties", properties);
            
            return payload;
        }
        
        private JsonObject addApplicationTraits(JsonObject properties) {
            JsonObject appTraitsObject = ActivationInfoCollector.traitsWithAppInfo();

            for (Map.Entry<String, JsonElement> entry : appTraitsObject.entrySet()) {
                properties.add(entry.getKey(), entry.getValue());
            }
            
            return properties;
        }
    }
}
