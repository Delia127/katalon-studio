package com.kms.katalon.composer.webui.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	//RemoteWebDriverExecutionHandler
	public static final String DIA_REMOTE_SERVER_URL_TITLE = "Remote web driver";
	public static final String DIA_REMOTE_SERVER_URL_MESSAGE = "Please enter the remote web driver server url";
	
	//WebUiExecutionPreferencePage
	public static final String PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT = "Default wait when IE hangs (in seconds):";
	
	// RemoteWebPreferencePage
	public static final String LBL_REMOTE_SERVER_URL = "Remote web server url";
	public static final String LBL_REMOTE_SERVER_TYPE = "Remote web server type";
	
	public static final String LBL_DEBUG_PORT = "Debugging Port";
	public static final String LBL_DEBUG_HOST = "Debugging Host";
	public static final String LBL_DESIRED_CAP = "Desired capabilities (additional WebDriver preferences):";
	
	//WebUiSettingsPreferencePage
	public static final String PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT = "Default page load timeout (in seconds)";
	public static final String PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT = "Wait until the page is loaded";
	public static final String PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION = "After timeout, continue to run";
	public static final String PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BIGGER_FROM_X = "Invalid number, number value must be >= {0}";
	
}
