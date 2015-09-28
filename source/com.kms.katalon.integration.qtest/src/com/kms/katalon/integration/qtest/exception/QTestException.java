package com.kms.katalon.integration.qtest.exception;

public abstract class QTestException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2706156244264286479L;

    public QTestException(String message) {
        super(message);
    }
}
