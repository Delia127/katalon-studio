package com.kms.katalon.objectspy.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// ObjectSpyDialog
	public static final String DIA_BTN_ADD = ADD;
	public static final String DIA_BTN_START_BROWSER = "Start Browser";
	public static final String DIA_TITLE_OBJ_SPY = "Object Spy";
	public static final String DIA_LBL_NAME = NAME;
	public static final String DIA_LBL_TYPE = "Type";
	public static final String DIA_MENU_CONTEXT_VERIFY = "Verify";
	public static final String DIA_FINDING_ELEMENTS = "Finding elements";
	public static final String DIA_FINDING_ELEMENTS_W_XPATH = "Finding elements with xpath ''{0}''";
	public static final String DIA_ERROR_MSG_CANNOT_PARSE_XPATH = "Cannot parse xpath";
	public static final String DIA_ERROR_REASON_INVALID_XPATH = "xpath is invalid ({0})";
	public static final String DIA_REFRESHING_DOM_EXPLORER = "Refresing DOM Explorer";
	public static final String DIA_TOOLITEM_TIP_NEW_PAGE_ELEMENT = "New page element";
	public static final String DIA_TOOLITEM_TIP_NEW_FRAME_ELEMENT = "New frame element";
	public static final String DIA_TOOLITEM_TIP_NEW_ELEMENT = "New element";
	public static final String DIA_TOOLITEM_TIP_REMOVE_ELEMENT = "Delete element";
	public static final String DIA_TOOLITEM_TIP_ADD_ELEMENT_TO_OBJECT_REPO = "Add to Object Repository";
	public static final String DIA_COL_NAME = NAME;
	public static final String DIA_COL_VALUE = VALUE;
	public static final String ERROR_TITLE = ERROR;
	public static final String DIA_WARN_SELECT_PARENT_FOLDER_FOR_ELEMENT = "Please select an object repository folder before adding element";

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
			+ APP_NAME
			+ " application build path>\\resources\\extensions\\IE\\{0}\\setup.exe\" file.";

	// DOMUtils
	public static final String UTIL_EXC_ATTR_NUMBER_DOES_NOT_MATCH = "Attributes number do not match";
	public static final String UTIL_EXC_ELEM_ATTR_NOT_FOUND = "No attribute name ''{0}'' found on this element";
	public static final String UTIL_EXC_ATTR_VAL_DOES_NOT_MATCH = "Attribute name ''{0}'' values do not match: ''{1}'' and ''{2}''";
}
