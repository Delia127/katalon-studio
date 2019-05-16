package com.kms.katalon.composer.webservice.postman;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Urlencoded {
    private String key;

    private String value;
    
    private String description;

    private boolean disabled;
    
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @JsonProperty("disabled")
    public boolean isDisabled() {
        return disabled;
    }
    @JsonProperty("disabled")
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }
}
