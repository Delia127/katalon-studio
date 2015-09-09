package com.kms.katalon.integration.qtest.exception;

import java.io.IOException;

public class QTestIOException extends QTestException {
    /**
     * 
     */
    private static final long serialVersionUID = -3286268475014623622L;
    
    public QTestIOException(String message) {
        super(message);
    }

    public QTestIOException(IOException ex) {
        super(ex.getMessage());
    }
}
