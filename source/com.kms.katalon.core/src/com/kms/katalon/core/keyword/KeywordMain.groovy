package com.kms.katalon.core.keyword

import groovy.transform.CompileStatic

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.ExceptionsUtil


public class KeywordMain {
    private static KeywordLogger logger = KeywordLogger.getInstance();

    @CompileStatic
    public static stepFailed(String message, FailureHandling flHandling, String reason, Map<String, String> attributes = null) throws StepFailedException {
        String failedMessage = buildReasonMessage(message, reason).toString()
        switch (flHandling) {
            case FailureHandling.OPTIONAL:
                logger.logWarning(failedMessage, attributes);
                break;
            case FailureHandling.CONTINUE_ON_FAILURE:
                logger.logFailed(failedMessage, attributes);
                ErrorCollector.getCollector().addError(new StepFailedException(failedMessage));
                break;
            case FailureHandling.STOP_ON_FAILURE:
                logger.logFailed(failedMessage, attributes);
                throw new StepFailedException(failedMessage.toString());
        }
    }

    @CompileStatic
    protected static StringBuilder buildReasonMessage(String message, String reason) {
        StringBuilder failMessage = new StringBuilder(message);
        if (reason != null) {
            failMessage.append(" (Root cause: ");
            failMessage.append(reason);
            failMessage.append(")");
        }
        return failMessage
    }

    @CompileStatic
    public static runKeyword(Closure closure, FailureHandling flowControl, String errorMessage) {
        try {
            return closure.call();
        } catch (Throwable e) {
            stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e));
        }
    }
    
    @CompileStatic
    public static int runKeywordAndReturnInt(Closure closure, FailureHandling flowControl, String errorMessage) {
        try {
            return (int) closure.call();
        } catch (Throwable e) {
            stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e));
        }
        return -1;
    }
}
