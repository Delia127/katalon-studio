package com.kms.katalon.tracking.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.kms.katalon.application.utils.ServerAPICommunicationUtil;

public class TrackingApiService {

    private static TrackingApiService instance;
    
    private TrackingApiService() {
        
    }
    
    public static TrackingApiService getInstance() {
        if (instance == null) {
            instance = new TrackingApiService();
        }
        return instance;
    }
    
    public void post(String payload) throws IOException, GeneralSecurityException {
        ServerAPICommunicationUtil.post("/product/usage", payload, false);
    }
}
