package com.kms.katalon.composer.windows.record.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class WindowsRecordedElement {

    @SerializedName("Type")
    private String type;

    @SerializedName("Attributes")
    private Map<String, String> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
