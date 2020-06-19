package com.kms.katalon.entity.webservice;

public class FormDataBodyParameter {
    
    public static final String PARAM_TYPE_TEXT = "Text";
    
    public static final String PARAM_TYPE_FILE = "File";

    private String name;
    
    private String value;
    
    private String type = PARAM_TYPE_TEXT;
    
    private String contentType = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
