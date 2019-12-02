package com.kms.katalon.integration.analytics.exceptions;
    
public class AnalyticsApiExeception extends Exception {
    private static final long serialVersionUID = 8383665386658750568L;
    
    public AnalyticsApiExeception(Throwable e) {
        super(e);
    }
    
    public AnalyticsApiExeception(String message) {
        super(message);
    }
    
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
    
    public static AnalyticsApiExeception wrap(Exception e) {
        if (e instanceof AnalyticsApiExeception) {
            return (AnalyticsApiExeception) e;
        } else {
            return new AnalyticsApiExeception(e);
        }
    }
}
