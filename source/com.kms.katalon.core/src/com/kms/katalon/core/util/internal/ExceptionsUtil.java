package com.kms.katalon.core.util.internal;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.groovy.runtime.StackTraceUtils;

import com.google.common.base.Throwables;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.main.ScriptEngine;

import groovy.lang.MissingPropertyException;

public class ExceptionsUtil {
    
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(Script[0-9]{13})\\.run\\(Script[0-9]{13}\\.groovy");
    
    public static String getMessageForThrowable(Throwable t) {
        if (t == null) {
            return "";
        }
        return getExceptionMessage(t);
    }

    private static String getExceptionMessage(Throwable throwable) {
        if (throwable instanceof MissingPropertyException) {
            return getExceptionMessage((MissingPropertyException) throwable);
        } else {
            return throwable.getClass().getName()
                    + (throwable.getMessage() != null ? (": " + throwable.getMessage()) : "");
        }
    }

    private static String getExceptionMessage(MissingPropertyException exception) {
        return "Variable '" + exception.getProperty() + "' is not defined for test case.";
    }

    public static String getStackTraceForThrowable(Throwable t) {
        t = StackTraceUtils.deepSanitize(t);
        String stackTrace = Throwables.getStackTraceAsString(t);
        StringBuffer resultString = new StringBuffer();
        Matcher regexMatcher = SCRIPT_PATTERN.matcher(stackTrace);
        while (regexMatcher.find()) {
            String replacement = "(" + ScriptEngine.getTestCaseName(regexMatcher.group(1) + ".groovy");
            regexMatcher.appendReplacement(resultString, replacement);
        }
        regexMatcher.appendTail(resultString);
        return resultString.toString();
    }
}
