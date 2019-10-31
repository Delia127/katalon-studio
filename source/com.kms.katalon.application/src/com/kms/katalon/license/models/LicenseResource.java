package com.kms.katalon.license.models;

public class LicenseResource {
    
    private License license;
    
    private String message;
    
    public void setLicense(License license) {
        this.license = license;
    }
    
    public License getLicense() {
        return license;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

}
