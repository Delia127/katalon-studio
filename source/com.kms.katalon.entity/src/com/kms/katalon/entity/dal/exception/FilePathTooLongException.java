package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import com.kms.katalon.entity.constants.StringConstants;

public class FilePathTooLongException extends Exception {
	private static final long serialVersionUID = 3835455478158572463L;
	
	public FilePathTooLongException(int currentLength, int limitLength) {
		super(MessageFormat.format(StringConstants.EXC_CANNOT_SAVE_FILE_PATH_LENG_LIMIT_EXCEEDED, currentLength, limitLength));
	}
	
	public FilePathTooLongException(int currentChildLength, String childName, int limitLength) {
		super(MessageFormat.format(StringConstants.EXC_CANNOT_SAVE_CHILD_ENTITY_FILE_PATH_LIMIT_EXCEEDED, childName, currentChildLength, limitLength));
	}
}
