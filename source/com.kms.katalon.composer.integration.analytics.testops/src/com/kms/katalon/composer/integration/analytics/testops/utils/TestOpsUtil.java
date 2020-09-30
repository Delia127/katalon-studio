package com.kms.katalon.composer.integration.analytics.testops.utils;

public class TestOpsUtil {
    
    
    /**
     * remove all unnecessary '/' character that may cause calling api tobe failed.
     * @param url the raw url that need to be truncated
     * @return the url that all trailing '/' removed
     */
    public static String truncateURL(String url) {
        return url.replaceAll("/+$", ""); 
    }
    
}
