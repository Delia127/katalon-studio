package com.kms.katalon.application.userprofile;

public enum UserExperienceLevel {
    FRESHER("Fresher"),
    EXPERIENCED("Experienced");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private UserExperienceLevel(String name) {
        this.name = name;
    }
}
