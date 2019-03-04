package com.kms.katalon.integration.analytics.exceptions;
    
public class AnalyticsApiExeception extends Exception {
    private static final long serialVersionUID = 8383665386658750568L;
    
    private Throwable e;
    
    public AnalyticsApiExeception(Throwable e) {
        this.e = e;
    }
    
    @Override
    public String getMessage() {
        return e.getMessage();
    }
    
    @Override
    public synchronized Throwable getCause() {
        return this;
    }
    
    @Override
    public StackTraceElement[] getStackTrace() {
        return e.getStackTrace();
    }
    
    @Override
    public String getLocalizedMessage() {
        return e.getLocalizedMessage();
    }
}
