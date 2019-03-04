package com.kms.katalon.entity.webservice;

public class UrlEncodedBodyParameter {
    
    private String name;
    
    private String value;
    
    public UrlEncodedBodyParameter(String name, String value){
    	this.name = name;
    	this.value = value;
    }
    
    public UrlEncodedBodyParameter(){
    	
    }

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
}
