package com.kms.katalon.license.models;

import java.util.Date;
import java.util.List;

public class License {
    
    private String jwtCode;
    
    private Date expirationDate;
    
    private Date renewTime;

    private String machineId;

    private List<Feature> features;

    private Long organizationId;

    private LicenseType licenseType;

    private boolean testing;
    
    private String publicKey;

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public Date getRenewTime() {
        return renewTime;
    }

    public void setRenewTime(Date renewTime) {
        this.renewTime = renewTime;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getJwtCode() {
        return jwtCode;
    }

    public void setJwtCode(String jwtCode) {
        this.jwtCode = jwtCode;
    }

    public LicenseType getType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = LicenseType.valueOf(licenseType);
    }

    public boolean isTesting() {
        return testing;
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    public boolean isEngineLicense() {
        for (Feature feature : features) {
            if (feature.getKey().contains("cli")) {
                return true;
            }
        }
        return false;
    }

    public boolean isKSELicense() {
        for (Feature feature : features) {
            if (feature.getKey().contains("gui")) {
                return true;
            }
        }
        return false;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
