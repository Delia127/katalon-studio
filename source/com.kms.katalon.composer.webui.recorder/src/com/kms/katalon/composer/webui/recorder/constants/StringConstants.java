package com.kms.katalon.composer.webui.recorder.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// RecorderDialog
	public static final String DIA_TITLE_RECORD = "Record";
	public static final String DIA_COL_ELEMENT = "Element";
	public static final String DIA_COL_ACTION_DATA = "Action Data";
	public static final String DIA_COL_ACTION = "Action";
	public static final String DIA_COL_NO = NO_;
	public static final String DIA_TOOLITEM_RESUME = "Resume";
	public static final String DIA_TOOLITEM_STOP = "Stop";
	public static final String DIA_TOOLITEM_PAUSE = "Pause";
	public static final String DIA_TOOLITEM_START = "Start";
	public static final String ERROR_TITLE = ERROR;
	public static final String DIA_LBL_CAPTURED_OBJECTS = "CAPTURED OBJECTS";
    public static final String DIA_LBL_OBJECT_PROPERTIES = "OBJECT PROPERTIES"; 
    public static final String DIA_LBL_RECORED_ACTIONS = "RECORDED ACTIONS";
    public static final String DIA_LBL_NAME = NAME;
    public static final String DIA_LBL_TAG = TAG;
    public static final String DIA_COL_NAME = NAME;
    public static final String DIA_COL_VALUE = VALUE;
    public static final String DIA_TITLE_CAPTURED_OBJECTS = "Captured Objects";
    public static final String DIA_MESSAGE_SELECT_ELEMENT = "Select target element";
    public static final String DIA_ERROR_MESSAGE_SELECT_ELEMENT = "Can only select a frame or element";
    public static final String JOB_GENERATE_SCRIPT_MESSAGE = "Generating scripts";
    public static final String JOB_ADDING_OBJECT = "Adding html elements into object repository";
    public static final String JOB_GENERATE_STATEMENT_MESSAGE = "Generating statements";
    public static final String DIA_MENU_ADD_BASIC_ACTION = "Basic Action";
    public static final String DIA_MENU_ADD_VALIDATION_POINT = "Add Validation Point";
    public static final String DIA_MENU_ADD_SYNCHRONIZE_POINT = "Add Synchronization Point";
    public static final String INSTANT_BROWSER_PREFIX = "Instant ";
    public static final String MENU_ITEM_INSTANT_BROWSERS = "Instant Browsers";
    public static final String HAND_INSTANT_BROWSERS_DIA_TITLE = "Instant browser";
    public static final String HAND_INSTANT_BROWSERS_DIA_MESSAGE = "Starting recorder with instant browser ''{0}''. " + 
            "Please make sure you have installed the browser''s add-on for Recorder. Do you want go to the add-on store right now to get the add-on for Recorder ?";
    public static final String HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE = "Don't show this dialog again";
    public static final String DIA_INSTANT_BROWSER_CHROME_RECORDER_EXTENSION_PATH = "<Katalon build path>/Resources/extensions/Chrome/Recorder Packed";
    
    public static final String RECORDER_CHROME_ADDON_URL = "https://chrome.google.com/webstore/detail/katalon-recorder/bnaalgpdhfjepeanejkicnidgbpbmkhh";
    public static final String RECORDER_FIREFOX_ADDON_URL = "https://addons.mozilla.org/en-US/firefox/addon/katalon-recorder/";

	// RecordHandler
	public static final String HAND_ERROR_MSG_CANNOT_GEN_TEST_STEPS = "Cannot generate test steps.";
	public static final String HAND_ERROR_MSG_PLS_SAVE_TEST_CASE = "Selected Test Case has unsaved changes. Please save the Test Case before proceeding.";
	public static final String HAND_ERROR_MSG_PLS_FIX_TEST_CASE = "Selected Test Case has errors. Please fix them before proceeding.";
	
	//HTMLActionDataBuilderDialog
	public static final String DIA_ACTION_DATA_LBL = "Action Data Builder";
	public static final String COLUMN_DATA_PARAM_NAME = "Param Name";
	public static final String COLUMN_DATA_PARAM_TYPE = "Param Type";
	public static final String COLUMN_DATA_VALUE_TYPE = "Value Type";
	public static final String COLUMN_DATA_VALUE = "Value";
	
	// RecorderPreferencePage
	public static final String PREF_LBL_INSTANT_BROWSER_PORT = "Port for instant browsers";
	public static final String PREF_LBL_INSTANT_BROWSER_DO_NOT_SHOW_WARNING_DIALOG = "Do not show warning dialog when starting";
	
	// Record Session
	public static final String DIALOG_CANNOT_START_IE_MESSAGE = "Recorder addon for IE is currently not installed on this machine. Katalon will now open the setup for Recorder addon for IE. After installing completed, please enable the addon in IE and start Recorder on IE again.";
	public static final String DIALOG_RUNNING_INSTANT_IE_MESSAGE = "Running Recorder addon on running IE. Please refresh the page for the Recorder addon to start working.";
}
