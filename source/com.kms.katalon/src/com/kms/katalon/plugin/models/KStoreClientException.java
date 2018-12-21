package com.kms.katalon.plugin.models;

public class KStoreClientException extends Exception {

    private static final long serialVersionUID = -6771424367155720285L;

    public KStoreClientException(String message) {
        super(message);
    }
    
    public KStoreClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
