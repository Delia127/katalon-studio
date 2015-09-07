package com.kms.katalon.constants.helper;

import com.kms.katalon.constants.IdConstants;

public class ConstantsHelper {
	private static final String URI_PREFIX = "platform:/plugin/";
	
	public static String getApplicationURI() {
		return URI_PREFIX + IdConstants.APPLICATION_ID;
	}
}
