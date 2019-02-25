package com.kms.katalon.plugin.models;

public class KStorePlugin {

    private long id;
    
    private String status;
    
    private String expirationDate;
    
    private String createdAt;
    
    private String updatedAt;
    
    private boolean active;
    
    private KStoreProduct product;
    
    private KStoreLicense licenseType;
    
    private KStorePluginVersion currentVersion;
    
    private KStorePluginVersion latestVersion;
    
    private boolean expired;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public KStoreProduct getProduct() {
        return product;
    }

    public void setProduct(KStoreProduct product) {
        this.product = product;
    }

    public KStoreLicense getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(KStoreLicense licenseType) {
        this.licenseType = licenseType;
    }

    public KStorePluginVersion getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(KStorePluginVersion currentVersion) {
        this.currentVersion = currentVersion;
    }

    public KStorePluginVersion getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(KStorePluginVersion latestVersion) {
        this.latestVersion = latestVersion;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
