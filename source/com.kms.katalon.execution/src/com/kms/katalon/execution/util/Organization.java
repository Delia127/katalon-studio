package com.kms.katalon.execution.util;

public class Organization {
    private static String id;
    
    //This should be called only once in application startup
    public static void set(String id) {
    	Organization.id = id;
    }
    
    public static String get() {
        return id;
    }
}
