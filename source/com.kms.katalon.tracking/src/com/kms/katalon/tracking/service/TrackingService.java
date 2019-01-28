package com.kms.katalon.tracking.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.tracking.model.TrackInfo;

public class TrackingService {
    
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void track(TrackInfo trackInfo) {
        
        executor.submit(() -> {
            try {
                String payload = buildEventPayload(trackInfo);
            
                sendEventPayload(payload);
            } catch (Exception e) {
                LogUtil.logError(e);
            }
        });
    }

    protected String buildEventPayload(TrackInfo trackInfo) {
        PayloadBuilder payloadBuilder = new PayloadBuilder();
        
        String payloadJson = payloadBuilder.build(trackInfo);
        
        return payloadJson;
    }
    
    private void sendEventPayload(String payload) throws IOException, GeneralSecurityException {
        TrackingApiService.getInstance().post(payload);
    }
    
    private class PayloadBuilder {
        
        public String build(TrackInfo trackInfo) {
            JsonObject payload = new JsonObject();
            
            payload = addUserInfo(payload, trackInfo);
            
            payload = addEventName(payload, trackInfo);
            
            payload = addEventProperties(payload, trackInfo);
            
            payload = addApplicationTraits(payload);
            
//            payload = addEventRam(payload,trackInfo);
//            
//            payload = addEventCPU(payload,trackInfo);
            
            return payload.toString();
        }
        
       

		private JsonObject addUserInfo(JsonObject payload, TrackInfo trackInfo) {
            String userId = getUserId(trackInfo.isAnonymous());
            payload.addProperty("userId", userId);
            return payload;
        }
        
        private String getUserId(boolean isAnonymous) {
            return isAnonymous ? "anonymous" : ApplicationInfo.getAppProperty("email");
        }
        
        private JsonObject addEventName(JsonObject payload, TrackInfo trackInfo) {
            payload.addProperty("event", trackInfo.getEventName());
            return payload;
        }
        
        private JsonObject addEventProperties(JsonObject payload, TrackInfo trackInfo) {
            payload.add("properties", trackInfo.getProperties());
            return payload;
        }
        
        private JsonObject addApplicationTraits(JsonObject payload) {
            JsonObject appTraitsObject = ActivationInfoCollector.traitsWithAppInfo();

            JsonUtil.mergeJsonObject(appTraitsObject, payload.get("properties").getAsJsonObject());
  
            return payload;
        }
//        private JsonObject addEventRam(JsonObject payload, TrackInfo trackInfo) {
////        	 String ram = getRam();
//             payload.addProperty("ram", "abc");
//             return payload;
//        }
//        private JsonObject addEventCPU(JsonObject payload, TrackInfo trackInfo) {
////       	 String cpu = getCPU(trackInfo);
//            payload.addProperty("cpu", "xyz");
//            return payload;
//		}
//        private String getRam(long ram) {
//            return null;
//        }
//        
//        private String getCPU(l cpu) {
//            return "";
//        }
        
    }
}
