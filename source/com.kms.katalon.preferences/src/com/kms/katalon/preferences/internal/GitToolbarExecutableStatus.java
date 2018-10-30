package com.kms.katalon.preferences.internal;

public class GitToolbarExecutableStatus {

    private static boolean isExecutable = false;
    
    public static boolean getValue() {
        return isExecutable;
    }
    
    public static void setValue(boolean value) {
        isExecutable = value;
    }
}
