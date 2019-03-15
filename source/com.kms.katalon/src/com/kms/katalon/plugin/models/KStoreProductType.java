package com.kms.katalon.plugin.models;

public class KStoreProductType {

    public static final String CUSTOM_KEYWORD = "Custom Keywords Plugin";
    
    private long id;
    
    private String name;

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
}
