package com.kms.katalon.core.webui.exception;

import com.kms.katalon.core.util.ExceptionsUtil;

public class AppiumStartException extends Exception {

    private static final long serialVersionUID = 1L;

    public AppiumStartException(String message) {
        super(message);
    }

    public AppiumStartException(Throwable t) {
        super(ExceptionsUtil.getMessageForThrowable(t), t);
    }
    
    public AppiumStartException(String message, Throwable t) {
        super(message, t);
    }
}
