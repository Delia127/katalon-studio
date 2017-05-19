package com.kms.katalon.composer.mobile.recorder.exceptions;

public class MobileRecordException extends Exception {
    private static final long serialVersionUID = -4163616722557090452L;

    public MobileRecordException(String message) {
        super(message);
    }
    
    public MobileRecordException(Throwable e) {
        super(e);
    }
}
