package com.kms.katalon.license.models;

import java.util.List;

public class AwsKatalonAmi {
    
    List<String> amiId;
    
    String license;
    
    public List<String> getAmiId() {
        return amiId;
    }
    
    public void setAmiId(List<String> amiId) {
        this.amiId = amiId;
    }
    
    public String getLicense() {
        return license;
    }
    
    public void setLicense(String license) {
        this.license = license;
    }
}
