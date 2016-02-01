package com.kms.katalon.composer.webui.recorder.action;

public enum HTMLActionDataType {
    Constant, Property;

    public static String[] stringValues() {
        HTMLActionDataType[] values = values();
        String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            stringValues[i] = values[i].toString();
        }
        return stringValues;
    }

    public static HTMLActionDataType fromValue(Object value) {
        if (value instanceof HTMLElementProperty) {
            return Property;
        }
        return Constant;
    }

    public Object getDefaultValue() {
        switch (this) {
        case Property:
            return new HTMLElementProperty("");
        default:
            return "";
        }
    }
}