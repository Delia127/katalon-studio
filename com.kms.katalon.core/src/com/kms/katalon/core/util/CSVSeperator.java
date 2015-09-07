package com.kms.katalon.core.util;


public enum CSVSeperator {
    COMMA("comma"),
    TAB("tab"),
    SEMICOLON("semicolon");
    
    private final String text;

    private CSVSeperator(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
    
    public static CSVSeperator fromValue(String value) {
        for (CSVSeperator type : values()) {
            if (type.toString().equals(value)) {
                return type;
            }
        }
        return valueOf(value);
    }
    
    public static String[] stringValues() {
        String[] stringValues = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].name();
        }
        return stringValues;
    }
}
