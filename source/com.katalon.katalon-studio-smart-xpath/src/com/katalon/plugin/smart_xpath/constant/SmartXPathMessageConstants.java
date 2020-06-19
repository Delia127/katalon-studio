package com.katalon.plugin.smart_xpath.constant;

import org.eclipse.osgi.util.NLS;

public class SmartXPathMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.katalon.plugin.smart_xpath.constant.SmartXpathMessages";

    public static String LABEL_EXCLUDE_OBJECTS_USED_WITH_KEYWORDS;

    public static String GRP_PRIORITIZE_SELECTION_METHODS_FOR_SELF_HEALING_EXECUTION;

    public static String LBL_TOGGLE_SELF_HEALING_EXECUTION_METHOD;

    public static String BUTTON_MOVE_UP_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

    public static String BUTTON_MOVE_DOWN_PRIORITIZE_SELF_HEALING_EXECUTION_ORDER;

    public static String COLUMN_SELECTION_METHOD;

    public static String COLUMN_KEYWORD;

    public static String COLUMN_SELECT;

    public static String XPATH_METHOD;

    public static String ATTRIBUTE_METHOD;

    public static String CSS_METHOD;

    public static String IMAGE_METHOD;

    public static String WEB_UI_BUILT_IN_KEYWORDS_CLASS_NAME;

    public static String WEB_UI_BUILT_IN_KEYWORDS_SIMPLE_CLASS_NAME;

    public static String ERROR_MESSAGE_WHEN_DUPLICATE_KEYWORD_METHOD;

    public static String LBL_SELF_HEALING;

    public static String LBL_ENABLE_SELF_HEALING;

    public static String LBL_DISABLE_SELF_HEALING;

    public static String LBL_SELF_HEALING_SETTINGS;

    public static String LBL_SELF_HEALING_INSIGHTS;

    public static String LBL_COL_TEST_OBJECT_ID;

    public static String LBL_COL_BROKEN_LOCATOR;

    public static String LBL_COL_PROPOSED_LOCATOR;

    public static String LBL_COL_RECOVERED_BY;

    public static String LBL_COL_SCREENSHOT;

    public static String LBL_PREVIEW_SCREENSHOT;

    public static String MSG_DOES_NOT_HAVE_SCREENSHOT;

    public static String MSG_SCREENSHOT_DOES_NOT_EXIST;

    public static String MSG_PLATFORM_DOES_NOT_SUPPORT_OPEN;

    public static String MSG_READ_ACCESS_DENIED;

    public static String BTN_APPROVE_PROPOSED_LOCATORS;

    public static String BTN_DISCARD_PROPOSED_LOCATORS;

    public static String MSG_RECOVER_BROKEN_TEST_OBJECTS_SUCCEEDED_SINGULAR;

    public static String MSG_RECOVER_BROKEN_TEST_OBJECTS_SUCCEEDED_PLURAL;

    public static String MSG_DISCARD_BROKEN_TEST_OBJECTS_SUCCEEDED_SINGULAR;

    public static String MSG_DISCARD_BROKEN_TEST_OBJECTS_SUCCEEDED_PLURAL;

    public static String MSG_RECOVER_BROKEN_TEST_OBJECTS_FAILED;

    public static String MSG_CANNOT_CREATE_SELF_HEALING_FOLDER;

    public static String LBL_TIPS_FOR_XPATH_TEST_DESIGN_SETTING;

    public static String LBL_TIPS_FOR_BASIC_TEST_DESIGN_SETTING;
    
    public static String TITLE_DISCARD_BROKEN_TEST_OBJECT_DIALOG;
    
    public static String MSG_DISCARD_BROKEN_TEST_OBJECT_DIALOG;

    public static String GRP_DEFAULT_LOCATOR_SELECTION_AREA;

    public static String SELF_HEALING_DOCUMENT_LINK;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, SmartXPathMessageConstants.class);
    }

    private SmartXPathMessageConstants() {
    }
}
