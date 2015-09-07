package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import com.kms.katalon.entity.constants.StringConstants;

public class WrongEntityVersionException extends Exception {
	private static final long serialVersionUID = 1665323720692360388L;

	public WrongEntityVersionException(String entityType, long entityID) {
		super(MessageFormat.format(StringConstants.EXC_WRONG_ENTITY_VER, entityType, entityID));
	}

	public WrongEntityVersionException(String message) {
		super(message);
	}
}
