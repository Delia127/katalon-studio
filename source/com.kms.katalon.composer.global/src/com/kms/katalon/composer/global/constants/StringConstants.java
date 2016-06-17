package com.kms.katalon.composer.global.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// GlobalVariableBuilder
	public static final String DIA_LBL_NAME = NAME + ":";
	public static final String DIA_ERROR_MSG_INVALID_VAR_NAME = "Variable's name is not a valid qualifier";
	public static final String DIA_LBL_DEFAULT_VALUE = "Default value:";
	public static final String DIA_NEW_VAR_TITLE = "New Variable";
	public static final String DIA_NEW_VAR_MSG = "Create a new variable";
	public static final String DIA_EDIT_VAR_TITLE = "Edit Variable";

    // VariableBuilderDialog
    public static final String DIA_TITLE_NEW_VAR = "New Variable";
    public static final String DIA_TITLE_EDIT_VAR = "Edit Variable";
    public static final String DIA_INFO_MSG_CREATE_NEW_VAR = "Create a new variable";
    public static final String DIA_INFO_MSG_EDIT_NEW_VAR = "Edit an existing variable";
    public static final String DIA_CTRL_VAR_INFO = "Variable names are case-sensitive.\n"
            + "A variable's name can be any letters, digits, the dollar sign \"$\", or the underscore charater \"_\".\n"
            + "However, it cannot start with a digit.";
    public static final String PA_COL_DEFAULT_VALUE_TYPE = "Init Value Type";
    public static final String PA_COL_DEFAULT_VALUE = "Init Value";
    public static final String PA_COL_DESCRIPTION = DESCRIPTION;
    public static final String PA_MSG_VARIABLE_NAME_EXIST = "This variable name is existed!";

	// GlobalVariablePart
	public static final String PA_BTN_TIP_ADD = ADD;
	public static final String PA_BTN_TIP_REMOVE = DELETE;
	public static final String PA_BTN_TIP_CLEAR = CLEAR;
	public static final String PA_BTN_TIP_EDIT = EDIT;
	public static final String PA_BTN_TIP_REFRESH = REFRESH;
	public static final String PA_MENU_CONTEXT_SHOW_PREFERENCES = "Show References";
	public static final String PA_COL_NAME = NAME;
	public static final String PA_COL_VALUE = VALUE;
	public static final String ERROR_TITLE = ERROR;
	public static final String PA_ERROR_MSG_UNABLE_TO_UPDATE_VAR_REFERENCES = "Unable to update references of the selected variable.";
	public static final String PA_ERROR_MSG_UNABLE_TO_SAVE_ALL_VAR = "Unable to save all global variables";
	public static final String PA_INFO_MSG_REQUIRE_SAVE_B4_REFRESH = "System needs to save before refreshing";
	public static final String PA_WARN_TITLE_INVALID_VAR = "Invalid Variable";
	public static final String PA_WARN_MSG_INVALID_VAR_NAME = "Variable''s name ''{0}'' is not a valid qualifier.";
	public static final String PA_WARN_MSG_DUPLICATE_VAR_NAME = "Variable''s name ''{0}'' is duplicated.";
}
