package com.kms.katalon.execution.util;

public class ApiKey {
    private static String key;
    
    //This should be called only once in application startup
    public static void set(String apiKey) {
    	ApiKey.key = apiKey;
    }
    
    public static String get() {
        return key;
    }
}
