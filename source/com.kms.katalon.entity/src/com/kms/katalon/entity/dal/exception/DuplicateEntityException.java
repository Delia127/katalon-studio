package com.kms.katalon.entity.dal.exception;

import com.kms.katalon.entity.constants.StringConstants;

public class DuplicateEntityException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicateEntityException(){
        super(StringConstants.EXC_DUPLICATE_ENTITY);
    }

}
