package com.kms.katalon.plugin.models;

import java.io.File;

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
    
    private boolean free;
    
    private boolean trial;
    
    private boolean paid;
    
    private boolean expired;
    
    private String location;
    
    private int remainingDay;
    
    private File file;
    
    private String downloadUrl;

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

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isTrial() {
        return trial;
    }

    public void setTrial(boolean trial) {
        this.trial = trial;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
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

    public int getRemainingDay() {
        return remainingDay;
    }

    public void setRemainingDay(int remainingDay) {
        this.remainingDay = remainingDay;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public String getDownloadUrl() {
		return downloadUrl;
	}
    
    public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
}
