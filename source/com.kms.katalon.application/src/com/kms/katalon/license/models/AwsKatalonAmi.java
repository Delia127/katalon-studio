package com.kms.katalon.license.models;

import java.util.List;

public class AwsKatalonAmi {
    
    List<String> amiIds;
    
    String kseLicense;
    
    String reLicense;
    
    public List<String> getAmiId() {
        return amiIds;
    }
    
    public void setAmiId(List<String> amiId) {
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
