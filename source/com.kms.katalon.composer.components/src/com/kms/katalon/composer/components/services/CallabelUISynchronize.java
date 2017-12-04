package com.kms.katalon.composer.components.services;

import java.util.concurrent.Callable;

public class CallabelUISynchronize {
    
    private Object returnedValue = null;
    
    private Exception exception = null;
    
    public static CallabelUISynchronize newInstance() {
        return new CallabelUISynchronize();
    }

    @SuppressWarnings("unchecked")
    public <T> T syncCallabelExec(Callable<T> callable) throws Exception {
        returnedValue = null;
        exception = null;
        UISynchronizeService.syncExec(() -> {
            try {
                returnedValue = (T) callable.call();
            } catch (Exception e) {
                exception = e;
            }
        });
        if (exception != null) {
            throw exception;
        }
        return (T) returnedValue;
    }
}
