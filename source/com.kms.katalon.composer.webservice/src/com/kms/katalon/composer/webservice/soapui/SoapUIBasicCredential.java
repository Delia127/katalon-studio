package com.kms.katalon.composer.webservice.soapui;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.core.util.internal.Base64;

public class SoapUIBasicCredential implements SoapUICredential {

    private static final String BASIC_AUTH_PREFIX_VALUE = "Basic ";

    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";

    private String username;

    private String password;

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Map<String, String> getAuthHeaders() {
        String authHeaderValue = BASIC_AUTH_PREFIX_VALUE + Base64.basicEncode(username, password);
        Map<String, String> authHeadersMap = new HashMap<>();
        authHeadersMap.put(HTTP_HEADER_AUTHORIZATION, authHeaderValue);
        return authHeadersMap;
    }
}
