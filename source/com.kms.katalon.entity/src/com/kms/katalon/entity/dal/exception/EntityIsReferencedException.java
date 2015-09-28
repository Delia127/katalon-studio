package com.kms.katalon.entity.dal.exception;

public class EntityIsReferencedException extends Exception {
    private static final long serialVersionUID = 1L;

    public EntityIsReferencedException(String message){
        super(message);
    }
}
