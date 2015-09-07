package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import com.kms.katalon.entity.constants.StringConstants;

public class NoEntityException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoEntityException(long pk, String entityType){
        super(MessageFormat.format(StringConstants.EXC_NO_ENTITY_W_ID_X_AND_TYPE_Y, pk, entityType));
    }
    
    public NoEntityException(String pk, String entityType){
        super(MessageFormat.format(StringConstants.EXC_NO_ENTITY_W_ID_X_AND_TYPE_Y, pk, entityType));
    }
    
    public NoEntityException(String message){
        super(message);
    }
}
