package com.kms.katalon.composer.keyword.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // DeleteKeywordAndPackageHandler
    public static final String ERROR_TITLE = ERROR;

    public static final String HAND_ERROR_MSG_UNABLE_TO_DELETE_KEYWORD = "Unable to delete keyword.";

    // NewKeywordHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_KEYWORD = "Unable to create Keyword.";

    // NewPackageHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_PACKAGE = "Unable to create package.";

    // OpenKeywordHandler
    public static final String HAND_REFRESHING_PROJECT = "Refreshing project";

    public static final String HAND_COLLECTING_CUSTOM_KEYWORD = "Collecting custom keywords";

    public static final String HAND_ERROR_MSG_CANNOT_OPEN_KEYWORD_FILE = "Cannot open keyword file.";

    // PastePackageHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_PASTE_DATA = "Unable to paste data.";

    public static final String HAND_ERROR_MSG_FILE_NOT_EXIST = "File does not exist!";

    public static final String HAND_TITLE_NAME_CONFLICT = "Name Conflict";

    public static final String HAND_MSG_KW_NAME_ALREADY_EXISTS = "Keyword ''{0}'' already exists. Please enter a new name.";

    // RenamePackageHandler
    public static final String HAND_ERROR_MSG_UNABLE_TO_RENAME_PACKAGE = "Unable to rename package.";

    // NewKeywordPopupMenuContribution
    public static final String MENU_CONTEXT_NEW_KEYWORD = KEYWORD;

    // NewPackagePopupMenuContribution
    public static final String MENU_CONTEXT_NEW_PACKAGE = PACKAGE;

    // NewRenamePackageDialog
    public static final String DIA_TITLE_PACKAGE = "Keyword Package";

    public static final String DIA_TITLE_RENAME = "Rename";

    public static final String DIA_MSG_NEW_PACKAGE = "Create Keyword Package";

    public static final String DIA_MSG_RENAME_PACKAGE = "Enter new name for the Package. All references to this Package are also updated.";

    public static final String DIA_MSG_INVALID_PACKAGE_NAME = "Invalid name! A package name must start with lower case English letters and can mix up with number, underscore, period (e.g., domain.package)";

    public static final String DIA_MSG_INVALID_KEYWORD_NAME = "Invalid name! A Keyword name should be nouns, in mixed case with the first letter of each internal word capitalized.";

    public static final String DIA_MSG_INVALID_JAVA_IDENTIFIER = "is not a valid Java identifier";

    // RenameKeywordDialog
    public static final String DIA_TITLE_KEYWORD = KEYWORD;

    public static final String DIA_MSG_RENAME_KEYWORD = "Enter new name for Keyword. All references to this Keyword are also updated.";

    // NewKeywordDialog
    public static final String DIA_MSG_CREATE_KEYWORD = "Create Keyword";

    public static final String DIA_TITLE_PACKAGE_SELECTION = "Package Selection";

    public static final String DIA_MSG_CHOOSE_A_PACKAGE = "Choose a package:";

    public static final String DIA_MSG_NO_PACKAGE = "Cannot find packages to select.";

    public static final String DIA_WARN_DEFAULT_PACKAGE = "The use of the default package is discouraged.";

    public static final String DIA_WARN_KEYWORD_START_WITH_LOWERCASE = "Keyword name is discouraged. By convention, Keyword names usually start with an uppercase letter";
}
