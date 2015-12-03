package com.kms.katalon.core.exception;

import groovy.lang.MissingPropertyException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtil {
    public static String getMessageForThrowable(Throwable t) {
        if (t == null) {
            return "";
        }
        return getExceptionMessage(t);
    }

    private static String getExceptionMessage(Throwable throwable) {
        if (throwable instanceof MissingPropertyException) {
            return getExceptionMessage((MissingPropertyException) throwable);
        } else if (throwable instanceof StepFailedException) {
            return getExceptionMessage((StepFailedException) throwable);
        } else if (throwable instanceof StepErrorException) {
            return getExceptionMessage((StepErrorException) throwable);
        } else {
            return throwable.getClass().getName()
                    + (throwable.getMessage() != null ? (": " + throwable.getMessage()) : "");
        }
    }

    private static String getExceptionMessage(StepFailedException exception) {
        return exception.getMessage() != null ? (exception.getMessage()) : "";
    }

    private static String getExceptionMessage(StepErrorException exception) {
        return exception.getMessage() != null ? (exception.getMessage()) : "";
    }

    private static String getExceptionMessage(MissingPropertyException exception) {
        return "Variable '" + exception.getProperty() + "' is not defined for test case.";
    }

    public static String getStackTraceForThrowable(Throwable t) {
        StringBuilder message = new StringBuilder("(Stack trace: ");
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        message.append(sw.toString());
        if (t.getCause() != null) {
            message.append(" (caused by: ");
            message.append(getStackTraceForThrowable(t.getCause()));
            message.append(")");
        }
        message.append(")");
        return message.toString();
    }
}
