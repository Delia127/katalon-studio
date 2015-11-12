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
        if (fName == null) {
            fName = "";
        }
        return fName;
    }
    
    public String getValue() {
        if (fValue == null) {
            fValue = "";
        }
        return fValue;
    }
    
    public void setValue(String value) {
        fValue = value;
    }
}
