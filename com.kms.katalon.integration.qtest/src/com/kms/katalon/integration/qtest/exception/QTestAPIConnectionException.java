package com.kms.katalon.integration.qtest.exception;

public class QTestAPIConnectionException extends QTestException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6214037688906896461L;
    
    public QTestAPIConnectionException(String message) {
        super(message);
    }

    public QTestAPIConnectionException(int reponse, String message) {
        super(message);
    }

}
