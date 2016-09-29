package com.kms.katalon.integration.kobiton.entity;

public class KobitonLoginInfo {
    private KobitonUser user;
    private String token;
    
    public KobitonUser getUser() {
        return user;
    }
    public void setUser(KobitonUser user) {
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
        return "KobitonLoginInfo [user=" + user + ", token=" + token + "]";
    }
}
