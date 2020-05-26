package com.katalon.plugin.smart_xpath.constant;

import org.eclipse.osgi.util.NLS;

public class SmartXPathMessageConstants extends NLS {
	private static final String BUNDLE_NAME = "com.katalon.plugin.smart_xpath.constant.SmartXpathMessages";

	public static String METHODS_PRIORITY_ORDER_VARIABLE;

	public static String EXCLUDE_KEYWORDS_VARIABLE;

	public static String SELF_HEALING_ENABLED_VARIABLE;

	public static String LABEL_EXCLUDE_OBJECTS_USED_WITH_KEYWORDS;

	public static String GRP_LBL_PRIORITIZE_SELECTION_METHODS_FOR_SELF_HEALING_EXECUTION;

	public static String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

	public static String BUTTON_MOVE_UP_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

	public static String BUTTON_MOVE_DOWN_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

	public static String COLUMN_SELECTION_METHOD;
	
	public static String COLUMN_KEYWORD;
	
	public static String COLUMN_DETECT_OBJECT_BY;

	public static String XPATH_METHOD;
	
	public static String ATTRIBUTE_METHOD;
	
	public static String CSS_METHOD;
	
	public static String IMAGE_METHOD;
	
	public static String TEST_OBJECT_ID_COLUMN;
	
	public static String BROKEN_LOCATOR_COLUMN;
	
	public static String PROPOSED_LOCATOR_COLUMN;
	
	public static String RECOVER_BY_COLUMN;
	
	public static String IMAGE_COLUMN;
	
	public static String APPROVE_COLUMN;
	
	public static String WEB_UI_BUILT_IN_KEYWORDS_CLASS_NAME;
	
	public static String WEB_UI_BUILT_IN_KEYWORDS_SIMPLE_CLASS_NAME;
	
	public static String ERROR_MESSAGE_WHEN_DUPLICATE_KEYWORD_METHOD;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, SmartXPathMessageConstants.class);
	}

	private SmartXPathMessageConstants() {
	}
}
