package com.kms.katalon.integration.analytics.util;

public class ApiKey {
    private static String get;
    
    //This should be called only once in application startup
    public static void set(String apiKey) {
    	ApiKey.get = apiKey;
    }
    
    public static String get() {
        return get;
    }
}
