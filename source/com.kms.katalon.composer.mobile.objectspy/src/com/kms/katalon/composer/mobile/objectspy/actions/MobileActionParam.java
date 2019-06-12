package com.kms.katalon.composer.mobile.objectspy.actions;

public class MobileActionParam {
    private String name;

    private Class<?> clazz;

    public MobileActionParam(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
