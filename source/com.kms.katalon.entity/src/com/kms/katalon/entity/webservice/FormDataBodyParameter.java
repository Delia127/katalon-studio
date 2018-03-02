package com.kms.katalon.entity.webservice;

import org.apache.commons.lang.StringUtils;

public class FormDataBodyParameter {
    
    public static final String FILE_PATH_SEPARATOR = ", ";
    
    public static final String PARAM_TYPE_TEXT = "Text";
    
    public static final String PARAM_TYPE_FILE = "File";

    private String name;
    
    private String value;
    
    private String type = PARAM_TYPE_TEXT;

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
}
