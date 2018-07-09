package com.kms.katalon.integration.analytics.entity;

public class AnalyticsLoginInfo {

    private AnalyticsUser user;

    private String token;

    public AnalyticsUser getUser() {
        return user;
    }

    public void setUser(AnalyticsUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AnalyticsUser [user=" + user + ", token=" + token + "]";
    }
}
