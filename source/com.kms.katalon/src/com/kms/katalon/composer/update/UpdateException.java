package com.kms.katalon.composer.update;

public class UpdateException extends Exception {

    private static final long serialVersionUID = -4509117526149313820L;

    private final Exception target;

    public UpdateException(Exception ex) {
        super(ex);
        this.target = ex;
    }

    public Exception getTarget() {
        return target;
    }
}