package com.kms.katalon.entity.dal.exception;

public class PrepareDataException extends Exception{ 
	private static final long serialVersionUID = 1L;
	private Exception ex;
	
	public PrepareDataException(Exception ex) {
		this.ex = ex;
	}
	
	public Exception getException() {
		return ex;
	}
}
