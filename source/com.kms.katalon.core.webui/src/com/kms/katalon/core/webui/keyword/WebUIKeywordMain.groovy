package com.kms.katalon.core.webui.keyword;

import groovy.transform.CompileStatic

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.ExceptionsUtil
import com.kms.katalon.core.webui.helper.screenshot.WebUIScreenCaptor

public class WebUIKeywordMain {

    @CompileStatic
    public static runKeyword(Closure closure, FailureHandling flowControl, boolean takeScreenShot, String errorMessage) {
        try {
            return closure.call();
        } catch (Throwable e) {
            stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e), takeScreenShot);
        }
    }
    
    // Add this for keywords that need to return int, as Groovy cannot automatically convert null to int
    @CompileStatic
    public static int runKeywordAndReturnInt(Closure closure, FailureHandling flowControl, boolean takeScreenShot, String errorMessage) {
        try {
            return (int) closure.call();
        } catch (Throwable e) {
            stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e), takeScreenShot);
        }
        return -1;
    }

    @CompileStatic
    public static stepFailed(String message, FailureHandling flHandling, String reason, boolean takeScreenShot)
            throws StepFailedException {
        KeywordMain.stepFailed(message, flHandling, reason, new WebUIScreenCaptor().takeScreenshotAndGetAttributes(takeScreenShot));
    }
}
