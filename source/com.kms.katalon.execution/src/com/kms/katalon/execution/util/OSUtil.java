package com.kms.katalon.execution.util;

public class OSUtil {
    private static String osName = System.getProperty("os.name").toLowerCase();
    
    public static String getExecutableExtension() {
        if (osName.contains("windows")) {
            return ".exe";
        }
        return "";
    }
}
