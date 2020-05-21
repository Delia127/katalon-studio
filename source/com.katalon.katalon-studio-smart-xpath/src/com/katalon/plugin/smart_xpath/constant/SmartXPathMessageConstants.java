package com.katalon.plugin.smart_xpath.constant;

import org.eclipse.osgi.util.NLS;

public class SmartXPathMessageConstants extends NLS {
	private static final String BUNDLE_NAME = "com.katalon.plugin.smart_xpath.constant.SmartXpathMessages";

	public static String LABEL_EXCLUDE_OBJECTS_USED_WITH_KEYWORDS;

	public static String GRP_LBL_PRIORITIZE_SELECTION_METHODS_FOR_SELF_HEALING_EXECUTION;

	public static String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

	public static String BUTTON_MOVE_UP_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

	public static String BUTTON_MOVE_DOWN_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

	public static String COLUMN_SELECTION_METHOD;
	
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

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, SmartXPathMessageConstants.class);
	}

	private SmartXPathMessageConstants() {
	}
}
