package com.kms.katalon.entity.project;

public enum QuickStartProjectType {
    WEBUI("Web UI"),
    MOBILE("Mobile"),
    WEBSERVICE("API"),
    BDD("BDD");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private QuickStartProjectType(String name) {
        this.name = name;
    }
}
