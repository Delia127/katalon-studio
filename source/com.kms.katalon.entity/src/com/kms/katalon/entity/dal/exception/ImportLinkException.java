package com.kms.katalon.entity.dal.exception;

import org.xml.sax.SAXException;


public class ImportLinkException extends SAXException {
	private static final long serialVersionUID = 7376870945540642032L;
	
	public ImportLinkException(Exception exception) {
        super(exception.getMessage());
        this.setStackTrace(exception.getStackTrace());
    }
	
    public ImportLinkException(String message) {
        super(message);
    }
}
