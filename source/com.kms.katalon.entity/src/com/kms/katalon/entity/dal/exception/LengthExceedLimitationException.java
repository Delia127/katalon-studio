package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import com.kms.katalon.entity.constants.StringConstants;

public class LengthExceedLimitationException extends Exception
{
	private static final long serialVersionUID = 1L;

	private String stringMessage;
    
    private static String fieldName;
    
    
    public LengthExceedLimitationException()
    {
    	super(MessageFormat.format(StringConstants.EXC_X_COULDNT_EXCEED_200_CHARS, fieldName));
    }
    
    public void setMessage(String input)
    {
    	stringMessage = input;
    }
    
    public String getMessage()
    {
    	return stringMessage;
    }
    
    public void setFieldName(String fieldInput)
    {
    	fieldName = fieldInput;
    }
    
    public String getFieldName()
    {
    	return fieldName;
    }
}
