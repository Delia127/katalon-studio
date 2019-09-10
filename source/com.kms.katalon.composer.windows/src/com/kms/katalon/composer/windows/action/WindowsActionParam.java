package com.kms.katalon.composer.windows.action;

public class WindowsActionParam {
    private String name;

    private Class<?> clazz;

    public WindowsActionParam(String name, Class<?> clazz) {
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
