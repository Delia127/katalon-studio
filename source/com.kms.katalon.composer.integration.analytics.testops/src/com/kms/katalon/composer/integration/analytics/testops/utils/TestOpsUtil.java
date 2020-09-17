package com.kms.katalon.composer.integration.analytics.testops.utils;

public class TestOpsUtil {
    
    public static String truncateURL(String url) {
        if (url == null) {
            return null;
        }
        
        StringBuilder builder = new StringBuilder(url);
        while (builder.length() > 0 && builder.charAt(builder.length() - 1) == '/') {
            builder.deleteCharAt(builder.length() - 1);
        }
        
        return builder.toString();
    }
    
}
