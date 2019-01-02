package com.kms.katalon.plugin.models;

import java.util.Date;

public class KStorePlugin {

    private long id;
    
    private String status;
    
    private Date expirationDate;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    private boolean active;
    
    private KStoreProduct product;
    
    private KStoreLicense licenseType;

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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
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
}
