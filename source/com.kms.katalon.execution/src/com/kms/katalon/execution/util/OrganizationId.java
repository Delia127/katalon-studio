package com.kms.katalon.execution.util;

public class OrganizationId {
    private static String id;
    
    //This should be called only once in application startup
    public static void set(String id) {
    	OrganizationId.id = id;
    }
    
    public static String get() {
        return id;
    }
}
