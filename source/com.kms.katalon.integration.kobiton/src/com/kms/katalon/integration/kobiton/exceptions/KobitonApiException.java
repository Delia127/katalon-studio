package com.kms.katalon.integration.kobiton.exceptions;

public class KobitonApiException extends Exception {
    private static final long serialVersionUID = 6919268398952482559L;

    private boolean error = false;

    private String message;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
