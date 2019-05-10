package com.kms.katalon.composer.webservice.postman;

import com.fasterxml.jackson.annotation.*;

public class Header {
    private String key;
    private String value;

    @JsonProperty("key")
    public String getKey() { return key; }
    @JsonProperty("key")
    public void setKey(String value) { this.key = value; }

    @JsonProperty("value")
    public String getValue() { return value; }
    @JsonProperty("value")
    public void setValue(String value) { this.value = value; }
}