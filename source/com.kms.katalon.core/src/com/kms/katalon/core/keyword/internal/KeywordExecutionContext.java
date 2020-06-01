package com.kms.katalon.core.keyword.internal;

public class KeywordExecutionContext {

    private static String runningKeyword;

    private static String runningPlatform;

    public static void saveRunningKeywordAndPlatform(String runningPlatform, String runningKeyword) {
        KeywordExecutionContext.runningPlatform = runningPlatform;
        KeywordExecutionContext.runningKeyword = runningKeyword;
    }

    public static String getRunningKeyword() {
        return runningKeyword;
    }

    public static void setRunningKeyword(String runningKeyword) {
        KeywordExecutionContext.runningKeyword = runningKeyword;
    }

    public static String getRunningPlatform() {
        return runningPlatform;
    }

    public static void setRunningPlatform(String runningPlatform) {
        KeywordExecutionContext.runningPlatform = runningPlatform;
    }

    public static boolean isRunningWebUI() {
        return runningPlatform.equals("web");
    }
}
