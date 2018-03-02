package com.kms.katalon.entity.webservice;

import org.apache.commons.lang3.StringUtils;

public class UrlEncodedBodyParameter implements EmptiableParameter {
    
    private String name;
    
    private String value;

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

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(name) && StringUtils.isBlank(value);
    }
}
