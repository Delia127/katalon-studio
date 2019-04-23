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
    
    private KStorePluginVersion latestCompatibleVersion;
    
    private boolean expired;
    
    private String location;

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

    public KStorePluginVersion getLatestCompatibleVersion() {
        return latestCompatibleVersion;
    }

    public void setLatestCompatibleVersion(KStorePluginVersion latestCompatibleVersion) {
        this.latestCompatibleVersion = latestCompatibleVersion;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KStorePlugin other = (KStorePlugin) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
