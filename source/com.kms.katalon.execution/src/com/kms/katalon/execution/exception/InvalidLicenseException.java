package com.kms.katalon.execution.exception;

public class InvalidLicenseException extends Exception {

    private static final long serialVersionUID = -8648109503310481624L;

    public InvalidLicenseException(String message) {
        super(message);
    }
    
    public InvalidLicenseException(Throwable cause) {
        super(cause);
    }
}
