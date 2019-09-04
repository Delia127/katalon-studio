package com.kms.katalon.execution.exception;

public class ActivationException extends Exception {

    private static final long serialVersionUID = 6857056918682650254L;

    public ActivationException(String message) {
        super(message);
    }
    
    public ActivationException(Throwable cause) {
        super(cause);
    }
}
