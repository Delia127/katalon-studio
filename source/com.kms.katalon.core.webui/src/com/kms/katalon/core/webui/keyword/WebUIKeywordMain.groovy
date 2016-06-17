package com.kms.katalon.core.webui.keyword;

import groovy.transform.CompileStatic

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.KeywordMain
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.ExceptionsUtil
import com.kms.katalon.core.webui.helper.screenshot.WebUIScreenCaptor

public class WebUIKeywordMain extends KeywordMain {

    @CompileStatic
    public static runKeyword(Closure closure, FailureHandling flowControl, boolean takeScreenShot, String errorMessage) {
        try {
            return closure.call();
        } catch (Throwable e) {
            if (e instanceof StepFailedException) {
                throw e;
            }
            stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e), takeScreenShot);
        }
    }

    @CompileStatic
    public static stepFailed(String message, FailureHandling flHandling, String reason, boolean takeScreenShot)
            throws StepFailedException {
        super.stepFailed(message, flHandling, reason, new WebUIScreenCaptor().takeScreenshotAndGetAttributes(takeScreenShot));
    }
}
