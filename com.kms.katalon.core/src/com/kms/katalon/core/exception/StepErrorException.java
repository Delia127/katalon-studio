package com.kms.katalon.core.exception;

public class StepErrorException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public StepErrorException(String message) {
        super(message);
    }
    
    public StepErrorException(Throwable t) {
        super(t);
    }
}
