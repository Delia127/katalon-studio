package com.kms.katalon.plugin.models;

import java.util.HashMap;
import java.util.Map;

public class KStoreUsernamePasswordCredentials implements KStoreCredentials {

    private String username;
    
    private String password;
    
    private String apiKey;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("username", username);
        headers.put("password", password);
        return headers;
    }
}
