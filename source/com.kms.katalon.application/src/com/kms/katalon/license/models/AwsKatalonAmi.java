package com.kms.katalon.license.models;

import java.util.List;

public class AwsKatalonAmi {
    
    private List<String> amiIds;
    
    private String kseLicense;
    
    private String reLicense;
    
    public List<String> getAmiIds() {
        return amiIds;
    }
    
    public void setAmiIds(List<String> amiId) {
        this.amiIds = amiId;
    }
    
    public String getKseLicense() {
        return kseLicense;
    }
    
    public void setKseLicense(String kseLicense) {
        this.kseLicense = kseLicense;
    }
    
    public String getReLicense() {
        return reLicense;
    }
    
    public void setReLicense(String reLicense) {
        this.reLicense = reLicense;
    }
}
