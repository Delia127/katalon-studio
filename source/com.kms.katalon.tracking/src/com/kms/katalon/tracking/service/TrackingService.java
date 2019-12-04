package com.kms.katalon.tracking.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.JsonObject;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.tracking.model.TrackInfo;

public class TrackingService {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public void track(TrackInfo trackInfo) {
        IPreferenceStore prefStore = PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
        boolean checkAllowUsage = prefStore.contains(PreferenceConstants.GENERAL_AUTO_CHECK_ALLOW_USAGE_TRACKING)
                ? prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_CHECK_ALLOW_USAGE_TRACKING) : true;
        boolean isPaidLicense = LicenseUtil.isPaidLicense();
        if (isPaidLicense && !checkAllowUsage) {
            return;
        } else {
            executor.submit(() -> {
                try {
                    String payload = buildEventPayload(trackInfo);
                    sendEventPayload(payload);
                } catch (Exception e) {
//                    LogUtil.logError(e);
                }
            });
        }

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

    }
}