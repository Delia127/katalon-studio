package com.kms.katalon.core.exception;


public class StepFailedException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public StepFailedException(String message) {
        super(message);
    }
    
    public StepFailedException(Throwable t) {
        super(ExceptionsUtil.getMessageForThrowable(t));
    }
}
