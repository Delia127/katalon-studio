package com.kms.katalon.composer.components.impl.model;

public class VariableDialogModel {

    private String fName;
    private String fValue;

    public VariableDialogModel(String name, String value) {
        fName = name;
        fValue = value;
    }
    
    public void setName(String name) {
        fName = name;
    }
    
    public String getName() {
        return fName;
    }
    
    public String getValue() {
        return fValue;
    }
    
    public void setValue(String value) {
        fValue = value;
    }
}
