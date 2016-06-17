package com.kms.katalon.composer.components.impl.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
    // AddMailRecipientDialog
    public static final String DIA_LBL_EMAIL = EMAIL + ":";
    public static final String DIA_DESC_PLS_ENTER_EMAIL_ADDR = "Please enter your email address."
            + "You can enter many email addresses by using \";\".\n For example: abcd@gmail.com; xyz@kms-technology.com";
    public static final String DIA_DESC_INVALID_EMAIL_ADDR = "Email is not valid.";
    public static final String DIA_DESC_DUPLICATED_EMAIL_ADDR = "Email(s) {0} has/have already existed in mail recipients.";
    public static final String DIA_ADD_EMAIL_RECIPIENT = "Add email recipient";

    // AbstractEntityDialog
    public static final String DIA_LBL_NAME = NAME;
    public static final String DIA_LBL_CREATE_NEW = "Create new";
    public static final String DIA_WINDOW_TITLE_NEW = "New";
    public static final String DIA_NAME_EXISTED = "Name already exists.";

    // TreeEntitySelectionDialog
    public static final String DIA_SEARCH_TEXT_DEFAULT_VALUE = "Enter text to search...";
    public static final String DIA_IMAGE_SEARCH_TOOLTIP = SEARCH;
    public static final String DIA_IMAGE_CLOSE_SEARCH_TOOLTIP = CLEAR;
    public static final String DIA_KEYWORD_SEARCH_ALL = "all";
    
    // MultiStatusErrorDialog
    public static final String DIA_TITLE_DETAILS = "Details";
    public static final String DIA_TITLE_REASON = "Reason:";

    // FolderTreeEntity
    public static final String TREE_FOLDER_TYPE_NAME = FOLDER;

    // KeywordTreeEntity
    public static final String TREE_KEYWORD_TYPE_NAME = KEYWORD;
    public static final String TREE_KEYWORD_KW = ENTITY_KW_KEYWORD;

    // PackageTreeEntity
    public static final String TREE_PACKAGE_TYPE_NAME = PACKAGE;
    public static final String TREE_PACKAGE_DEFAULT_LBL = DEFAULT_PACKAGE_NAME;

    // ReportTreeEntity
    public static final String TREE_REPORT_TYPE_NAME = REPORT;
    public static final String TREE_REPORT_KW = ENTITY_KW_REPORT;

    // TestCaseTreeEntity
    public static final String TREE_TEST_CASE_TYPE_NAME = TEST_CASE;
    public static final String TREE_TEST_CASE_KW = ENTITY_KW_TEST_CASE;

    // TestDataTreeEntity
    public static final String TREE_TEST_DATA_TYPE_NAME = TEST_DATA;
    public static final String TREE_TEST_DATA_KW = ENTITY_KW_TEST_DATA;

    // TestSuiteTreeEntity
    public static final String TREE_TEST_SUITE_TYPE_NAME = TEST_SUITE;
    public static final String TREE_TEST_SUITE_KW = ENTITY_KW_TEST_SUITE;

    // WebElementTreeEntity
    public static final String TREE_OBJECT_TYPE_NAME = OBJECT;
    public static final String TREE_OBJECT_KW = ENTITY_KW_TEST_OBJECT;
}
