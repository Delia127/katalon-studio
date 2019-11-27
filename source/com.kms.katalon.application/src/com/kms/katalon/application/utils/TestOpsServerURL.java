package com.kms.katalon.application.utils;

public class TestOpsServerURL {

    private static String serverURL;

    public static void set(String serverURL) {
        TestOpsServerURL.serverURL = serverURL;
    }

    public static String get() {
        return TestOpsServerURL.serverURL;
    }
}
