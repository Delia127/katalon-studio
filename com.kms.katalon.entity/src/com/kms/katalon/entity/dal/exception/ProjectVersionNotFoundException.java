package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import org.xml.sax.SAXException;

import com.kms.katalon.entity.constants.StringConstants;

public class ProjectVersionNotFoundException extends SAXException {
	private static final long serialVersionUID = 8544313624341226059L;

	public ProjectVersionNotFoundException(long projectID, String version) {
		super(MessageFormat.format(StringConstants.EXC_NO_PROJ_VER_W_ID_X_AND_VER_Y, projectID, version));
	}
	
	public ProjectVersionNotFoundException(String message) {
		super(message);
	}
}
