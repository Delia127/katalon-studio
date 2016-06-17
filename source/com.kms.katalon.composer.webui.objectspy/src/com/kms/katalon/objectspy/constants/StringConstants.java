package com.kms.katalon.objectspy.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // ObjectSpyDialog
    public static final String DIA_BTN_ADD = ADD;

    public static final String DIA_BTN_START_BROWSER = "Start";

    public static final String DIA_TITLE_OBJ_SPY = "Object Spy";

    public static final String DIA_LBL_NAME = NAME;

    public static final String DIA_LBL_TAG = TAG;

    public static final String DIA_MENU_CONTEXT_VERIFY = "Verify";

    public static final String DIA_FINDING_ELEMENTS = "Finding elements";

    public static final String DIA_FINDING_ELEMENTS_W_XPATH = "Finding elements with xpath ''{0}''";

    public static final String DIA_ERROR_MSG_CANNOT_PARSE_XPATH = "Cannot parse xpath";

    public static final String DIA_ERROR_REASON_INVALID_XPATH = "xpath is invalid ({0})";

    public static final String DIA_REFRESHING_DOM_EXPLORER = "Refresing DOM Explorer";

    public static final String DIA_TOOLITEM_NEW_PAGE = "New page";

    public static final String DIA_TOOLITEM_TIP_NEW_PAGE_ELEMENT = "New page element";

    public static final String DIA_TOOLITE_NEW_FRAME = "New frame";

    public static final String DIA_TOOLITEM_TIP_NEW_FRAME_ELEMENT = "New frame element";

    public static final String DIA_TOOLITEM_NEW_OBJECT = "New object";

    public static final String DIA_TOOLITEM_TIP_NEW_ELEMENT = "New object element";

    public static final String DIA_TOOLITEM_DELETE = DELETE;

    public static final String DIA_TOOLITEM_TIP_REMOVE_ELEMENT = "Delete element";

    public static final String DIA_TOOLITEM_TIP_ADD_ELEMENT_TO_OBJECT_REPO = "Add to Object Repository";

    public static final String DIA_COL_NAME = NAME;

    public static final String DIA_COL_VALUE = VALUE;

    public static final String ERROR_TITLE = ERROR;

    public static final String DIA_WARN_SELECT_PARENT_FOLDER_FOR_ELEMENT = "Please select an object repository folder before adding element";

    public static final String DIA_LBL_CAPTURED_OBJECTS = "CAPTURED OBJECTS";

    public static final String DIA_LBL_OBJECT_PROPERTIES = "OBJECT PROPERTIES";

    public static final String DIA_LBL_HTML_DOM = "HTML DOM";

    public static final String DIA_TXT_SEARCH_PLACE_HOLDER = "Enter xpath expression to search...";

    public static final String WARNING_NO_ELEMENT_FOUND_FOR_XPATH = "No element found for given xpath";

    public static final String WARNING_NO_PAGE_LOADED = "No web page loaded. Please start your desired browser and navigate to the web page";

    public static final String MENU_ITEM_INSTANT_BROWSERS = "Instant Browsers";

    public static final String HAND_INSTANT_BROWSERS_DIA_TITLE = "Instant browser";

    public static final String HAND_INSTANT_BROWSERS_DIA_MESSAGE = "Starting object spy inspection with instant browser ''{0}''. "
            + "Please make sure you have installed the browser''s add-on for Object Spy. Do you want go to the add-on store right now to get the add-on for Object Spy ?";

    public static final String DIA_INSTANT_BROWSER_CHROME_OBJECT_SPY_EXTENSION_PATH = "<Katalon build path>/Resources/extensions/Chrome/Object Spy Packed";
    
    public static final String OBJECT_SPY_CHROME_ADDON_URL = "https://chrome.google.com/webstore/detail/katalon-object-spy/gblkfilmbkbkjgpcoihaeghdindcanom";
    
    public static final String INSTANT_BROWSER_PREFIX = "Instant ";

    public static final String HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE = "Don't show this dialog again";

    // CheckboxTreeSelectionHelper
    public static final String TREE_ERROR_MSG_CONTENT_PROVIDER_IS_REQUIRED = "Content provider is required";

    // HTMLElementLabelProvider
    public static final String TREE_ELEMENT_TIP_ATTRIBUTES_CHANGED = "Element exists, but some of its attributes have been changed";

    public static final String TREE_ELEMENT_TIP_EXISTED = "Element exists";

    public static final String TREE_ELEMENT_TIP_MISSING = "Element is missing";

    public static final String TREE_ELEMENT_TIP_FOUND_MULTIPLE_ELEM = "Multiple elements are found";

    public static final String TREE_ELEMENT_TIP_IS_NOT_VERIFIED = "Element is not verified";

    public static final String TREE_ELEMENT_TIP_INVALID_XPATH = "The xpath of element is not valid";

    // BrowserNotSupportedException
    public static final String EXC_OBJ_SPY_FOR_BROWSER_IS_NOT_SUPPORTED = "Object spy for {0} is not supported.";

    // ExtensionNotFoundException
    public static final String EXC_EXTENSION_FOR_BROWSER_NOT_FOUND = "{0} for {1} not found.";

    // ExtensionNotFoundException
    public static final String EXC_ADDON_FOR_IE_IS_NOT_INSTALLED = APP_NAME
            + " {0} Addon for IE is not installed. To use {0} with IE, please install IE addon using the \"<"
            + APP_NAME + " application build path>\\resources\\extensions\\IE\\{0}\\setup.exe\" file.";

    // DOMUtils
    public static final String UTIL_EXC_ATTR_NUMBER_DOES_NOT_MATCH = "Attributes number do not match";

    public static final String UTIL_EXC_ELEM_ATTR_NOT_FOUND = "No attribute name ''{0}'' found on this element";

    public static final String UTIL_EXC_ATTR_VAL_DOES_NOT_MATCH = "Attribute name ''{0}'' values do not match: ''{1}'' and ''{2}''";

    // AddToObjectRepositoryDialog
    public static final String TITLE_ADD_TO_OBJECT_DIALOG = "Add Element to Object Repository";

    public static final String DIA_BTN_ADD_NEW_FOLDER = "New Folder";

    public static final String NEW_FOLDER_DEFAULT_NAME = "New Folder";

    public static final String DIA_MSG_PLS_SELECT_A_FOLDER = "Please select a target folder";

    public static final String DIA_MSG_PLS_SELECT_ELEMENT = "Please select elements to add to object repository";

    public static final String DIA_LBL_SELECT_A_DESTINATION_FOLDER = "Select a destination folder";

    public static final String PREF_LBL_INSTANT_BROWSER_PORT = "Port for instant browsers";
    
    public static final String PREF_LBL_INSTANT_BROWSER_PORT_DO_NOT_SHOW_WARNING_DIALOG = "Do not show warning dialog when starting";
}
