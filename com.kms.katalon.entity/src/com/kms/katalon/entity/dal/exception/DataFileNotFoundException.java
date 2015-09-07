package com.kms.katalon.entity.dal.exception;

import java.text.MessageFormat;

import org.xml.sax.SAXException;

import com.kms.katalon.entity.constants.StringConstants;

public class DataFileNotFoundException extends SAXException {
	private static final long serialVersionUID = 3493323731900293619L;

	public DataFileNotFoundException(long projectID, String dataFileGUID) {
		super(MessageFormat.format(StringConstants.EXC_NO_DATA_FILE_W_PROJ_ID_X_AND_GUID_Y, projectID, dataFileGUID));
	}
	
	public DataFileNotFoundException(String message) {
		super(message);
	}
}
