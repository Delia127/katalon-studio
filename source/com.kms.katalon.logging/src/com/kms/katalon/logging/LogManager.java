package com.kms.katalon.logging;


public class LogManager {
    private static SystemLogger errorLogger;
    private static SystemLogger outputLogger;
    
    public static void active() {
        System.setErr(getErrorLogger());
        System.setOut(getOutputLogger());
    }
    
    public static SystemLogger getErrorLogger() {
        if (errorLogger == null) {
            errorLogger = new SystemLogger(System.err, LogMode.LOG);
        }
        return errorLogger;
    }
    
    public static SystemLogger getOutputLogger() {
        if (outputLogger == null) {
            outputLogger = new SystemLogger(System.out, LogMode.CONSOLE);
        }
        return outputLogger;
    }
    
    public static void stop() {
        getErrorLogger().close();
        getOutputLogger().close();
    }
}
