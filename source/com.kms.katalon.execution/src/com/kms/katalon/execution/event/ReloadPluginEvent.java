package com.kms.katalon.execution.event;

public class ReloadPluginEvent {

    private String apiKey;

    public ReloadPluginEvent(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
