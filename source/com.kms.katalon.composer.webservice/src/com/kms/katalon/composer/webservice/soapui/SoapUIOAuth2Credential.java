package com.kms.katalon.composer.webservice.soapui;

import java.util.HashMap;
import java.util.Map;

public class SoapUIOAuth2Credential implements SoapUICredential {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    private static final String BEARER = "Bearer ";
    
    private String profileName;
    
    private String accessToken;
    
    public SoapUIOAuth2Credential(String profileName) {
        this.profileName = profileName;
    }

    protected String getProfileName() {
        return profileName;
    }
    
    protected String getAccessToken() {
        return accessToken;
    }

    protected void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, String> getAuthHeaders() {
        Map<String, String> authHeadersMap = new HashMap<>();
        authHeadersMap.put(AUTHORIZATION_HEADER, BEARER + accessToken);
        return authHeadersMap;
    }

}
