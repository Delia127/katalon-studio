package com.kms.katalon.composer.webui.recorder.action;

public class HTMLElementProperty {
    private String name;
    
    public HTMLElementProperty(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
