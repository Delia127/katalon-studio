package com.kms.katalon.integration.analytics.entity;

public class AnalyticsExecution {
    
    private Long id;

    private String webUrl;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
