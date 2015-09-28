package com.kms.katalon.core.keyword

import groovy.transform.CompileStatic

import com.kms.katalon.core.exception.ExceptionsUtil
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.model.FailureHandling


public class KeywordMain {
	private static KeywordLogger logger = KeywordLogger.getInstance();

	@CompileStatic
	public static stepFailed(String message, FailureHandling flHandling, String reason, Map<String, String> attributes = null) throws StepFailedException {
		StringBuilder failMessage = buildReasonMessage(message, reason)
		if (flHandling == null || flHandling != FailureHandling.OPTIONAL) {
			logger.logFailed(failMessage.toString(), attributes);
			KeywordExceptionHandler.throwExceptionIfStepFailed(failMessage.toString(), flHandling);
		} else {
			logger.logWarning(failMessage.toString(), attributes);
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
			if (e instanceof StepFailedException) {
				throw (StepFailedException) e;
			}
			stepFailed(errorMessage, flowControl, ExceptionsUtil.getMessageForThrowable(e));
		}
	}
}
