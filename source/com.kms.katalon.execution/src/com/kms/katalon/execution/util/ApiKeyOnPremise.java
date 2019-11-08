package com.kms.katalon.execution.util;

public class ApiKeyOnPremise {
    private static String key;

    // This should be called only once in application startup
    // Use for Katalon TestOps On-premise
    public static void set(String apiKey) {
        ApiKeyOnPremise.key = apiKey;
    }

    public static String get() {
        return key;
    }
}
