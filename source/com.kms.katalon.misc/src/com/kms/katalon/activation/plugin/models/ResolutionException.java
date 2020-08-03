package com.kms.katalon.activation.plugin.models;

public class ResolutionException extends Exception {

    private static final long serialVersionUID = 5818833864417554694L;

    public ResolutionException(String message) {
        super(message);
    }
    
    public ResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
