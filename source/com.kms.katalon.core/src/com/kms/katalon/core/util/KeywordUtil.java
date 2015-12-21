package com.kms.katalon.core.util;

import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.logging.ErrorCollector;
import com.kms.katalon.core.logging.KeywordLogger;

public class KeywordUtil {
    /**
     * Mark a keyword to be failed and continue execution
     * @param message fail message
     */
    public static void markFailed(String message) {
        ErrorCollector.getCollector().addError(new StepFailedException(message));
    }
    
    /**
     * Mark a keyword to be failed and stop execution
     * @param message fail message
     */
    public static void markFailedAndStop(String message) {
        throw new StepFailedException(message);
    }
    
    /**
     * Log message as info
     * @param message log info message
     */
    public static void logInfo(String message) {
        KeywordLogger.getInstance().logInfo(message);
    }
    
    /**
     * Mark a keyword to be warning
     * @param message warning message
     */
    public static void markWarning(String message) {
        KeywordLogger.getInstance().logWarning(message);
    }
}
