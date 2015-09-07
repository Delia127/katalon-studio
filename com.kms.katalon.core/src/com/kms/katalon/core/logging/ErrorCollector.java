package com.kms.katalon.core.logging;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerInvocationException;

import com.kms.katalon.core.exception.StepErrorException;
import com.kms.katalon.core.exception.StepFailedException;

public class ErrorCollector {
	private List<Throwable> errors;
	private static ErrorCollector _instance;

	private ErrorCollector() {
		errors = new ArrayList<Throwable>();
	}

	public List<Throwable> getErrors() {
		return errors;
	}

	public List<Throwable> getCoppiedErrors() {
		return new ArrayList<Throwable>(errors);
	}

	public static ErrorCollector getCollector() {
		if (_instance == null) {
			_instance = new ErrorCollector();
		}
		return _instance;
	}

	public void clearErrors() {
		errors.clear();
	}

	public void addError(Throwable t) {
		if (!getErrors().contains(t)) {
			errors.add(t);
		}
	}

	public boolean containsErrors() {
		return errors.size() > 0;
	}

	public boolean isFirstErrorFailed() {
		if (containsErrors()) {
			Throwable error = errors.get(0);
			return isErrorFailed(error);
		}
		return false;
	}

	public static boolean isErrorFailed(Throwable error) {
		if (error instanceof StepFailedException || error instanceof AssertionError) {
			return true;
		} else if (error instanceof InvokerInvocationException && error.getCause() != null) {
			return isErrorFailed(error.getCause());
		}
		return false;
	}
	
	public static void throwError(Throwable error) {
		if (ErrorCollector.isErrorFailed(error)) {
			if (error instanceof InvokerInvocationException) {
				throw (InvokerInvocationException) error;
			} else if (error instanceof AssertionError){
				throw (AssertionError) error;
			} else if (error instanceof StepFailedException) {
				throw (StepFailedException) error;
			} else {
				throw new StepFailedException(error);
			}
		} else {
			throw new StepErrorException(error);
		}
	}

	public boolean isLastErrorFailed() {
		if (containsErrors()) {
			Throwable error = errors.get(errors.size() - 1);
			return isErrorFailed(error);
		}
		return false;
	}

	public Throwable getLastError() {
		if (containsErrors()) {
			return errors.get(errors.size() - 1);
		}
		return null;
	}

	public Throwable getFirstError() {
		if (containsErrors()) {
			return errors.get(0);
		}
		return null;
	}

	public static void checkErrorAfterRunningCustomKeywordMethod(String keywordName) {
		if (ErrorCollector.getCollector().containsErrors()) {
			Throwable throwable = ErrorCollector.getCollector().getFirstError();
			if (ErrorCollector.getCollector().isFirstErrorFailed()) {
				KeywordLogger.getInstance().logMessage(LogLevel.FAILED,
						ErrorCollector.getCollector().getFirstError().getMessage());
			} else {
				KeywordLogger.getInstance().logMessage(LogLevel.ERROR, throwable.getMessage());
			}
		} else {
			KeywordLogger.getInstance().logMessage(LogLevel.PASSED, keywordName + " is PASSED");
		}
	}
}
