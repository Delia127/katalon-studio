package com.kms.katalon.core.configuration;

import com.kms.katalon.core.constants.StringConstants;

public class RunConfiguration {
	public static final String LOG_FILE_PATH_PROPERTY = StringConstants.CONF_PROPERTY_LOG_FILE_PATH;
	public static final String TIMEOUT_PROPERTY = StringConstants.CONF_PROPERTY_TIMEOUT;
	public static final String PROJECT_DIR_PROPERTY = StringConstants.CONF_PROPERTY_PROJECT_DIR;

	public static final String HOST_NAME = StringConstants.CONF_PROPERTY_HOST_NAME;
	public static final String HOST_OS = StringConstants.CONF_PROPERTY_HOST_OS;
	public static final String EXCUTION_SOURCE = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE;
	public static final String EXCUTION_SOURCE_NAME = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE_NAME;
	public static final String EXCUTION_SOURCE_ID = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE_ID;
	public static final String EXCUTION_SOURCE_DESCRIPTION = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE_DESCRIPTION;
	public static final String CUSTOME_EXECUTION_PROFILE = StringConstants.CONF_PROPERTY_CUSTOM_PROPERTY_PROFILE;
	

	public static String getProperty(String key) {
		return System.getProperty(key);
	}

	public static String getLogFilePath() {
		return getProperty(LOG_FILE_PATH_PROPERTY);
	}

	public static int getTimeOut() {
		return Integer.parseInt(getProperty(TIMEOUT_PROPERTY));
	}

	public static String getProjectDir() {
		return getProperty(PROJECT_DIR_PROPERTY);
	}

	public static String getHostName() {
		return getProperty(HOST_NAME);
	}

	public static String getOS() {
		return getProperty(HOST_OS);
	}

	public static String getExecutionSource() {
		return getProperty(EXCUTION_SOURCE);
	}

	public static String getExecutionSourceName() {
		return getProperty(EXCUTION_SOURCE_NAME);
	}

	public static String getExecutionSourceId() {
		return getProperty(EXCUTION_SOURCE_ID);
	}

	public static String getExecutionSourceDescription() {
		return getProperty(EXCUTION_SOURCE_DESCRIPTION);
	}
	
	public static String getCustomExecutionProfile() {
	    return getProperty(CUSTOME_EXECUTION_PROFILE);
	}

}
