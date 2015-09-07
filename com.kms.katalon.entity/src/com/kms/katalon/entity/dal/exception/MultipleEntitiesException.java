package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import com.kms.katalon.entity.constants.StringConstants;

public class MultipleEntitiesException extends Exception {
    private static final long serialVersionUID = 1L;

    public MultipleEntitiesException(long pk, String entityType){
        super(MessageFormat.format(StringConstants.EXC_MULTIPLE_ENTITIES_W_ID_X_AND_TYPE_Y, pk, entityType));
    }
    
    public MultipleEntitiesException(String pk, String entityType){
        super(MessageFormat.format(StringConstants.EXC_MULTIPLE_ENTITIES_W_ID_X_AND_TYPE_Y, pk, entityType));
    }
    
    public MultipleEntitiesException(String message){
        super(message);
    }
}
