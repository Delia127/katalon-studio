package com.kms.katalon.plugin.models;

import java.math.BigDecimal;

public class KStoreProduct {

    private long id;
    
    private String name;
    
    private String description;
    
    private String logo;
    
    private String repository;
    
    private String version;
    
    private String license;
    
    private String releaseDate;
    
    private String qaUrl;
    
    private String status;
    
    private String createdAt;
    
    private String updatedAt;
    
    private String overview;
    
    private BigDecimal price;
    
    private String url;
    
    private KStoreProductType productType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getQaUrl() {
        return qaUrl;
    }

    public void setQaUrl(String qaUrl) {
        this.qaUrl = qaUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public KStoreProductType getProductType() {
        return productType;
    }

    public void setProductType(KStoreProductType productType) {
        this.productType = productType;
    }
}
