package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import org.xml.sax.SAXException;

import com.kms.katalon.entity.constants.StringConstants;

public class WebElementNotFoundException extends SAXException {
	private static final long serialVersionUID = -3905990580447479369L;

	public WebElementNotFoundException(long projectID, String webElementGUID) {
		super(MessageFormat.format(StringConstants.EXC_NO_WEB_ELEMENT_W_PROJ_ID_X_AND_GUID_Y, projectID, webElementGUID));
	}
	
	public WebElementNotFoundException(String message) {
		super(message);
	}
}

