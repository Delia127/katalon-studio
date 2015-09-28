package com.kms.katalon.dal.fileservice.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// EntityService
	public static final String FS_EXC_FILE_NAME_CONTAIN_SPECIAL_CHAR = "A file name cannot contain any of the following characters: / \\ : * ? \" ' < > | ";

	// FileServiceConstant
	public static final String FS_ROOT_FOLDER_NAME_TEST_CASE = ROOT_FOLDER_NAME_TEST_CASE;
	public static final String FS_ROOT_FOLDER_NAME_TEST_SUITE = ROOT_FOLDER_NAME_TEST_SUITE;
	public static final String FS_ROOT_FOLDER_NAME_DATA_FILE = ROOT_FOLDER_NAME_DATA_FILE;
	public static final String FS_ROOT_FOLDER_NAME_OBJECT_REPOSITORY = ROOT_FOLDER_NAME_OBJECT_REPOSITORY;
	public static final String FS_ROOT_FOLDER_NAME_KEYWORD = ROOT_FOLDER_NAME_KEYWORD;
	public static final String FS_ROOT_FOLDER_NAME_REPORT = ROOT_FOLDER_NAME_REPORT;
	public static final String FS_FILE_NAME_GLOBAL_VARIABLE = FILE_NAME_GLOBAL_VARIABLE;

	// DataFileFileServiceDataProvider
	public static final String DP_EXC_NAME_CANNOT_BE_NULL_OR_EMPTY = "Name cannot be null or empty.";
	public static final String DP_EXC_NAME_ALREADY_EXISTED = "Name already existed.";

	// DataFileFileServiceManager
	public static final String MNG_NEW_TEST_DATA = "New Test Data";
	public static final String MNG_EXC_EXISTED_DATA_FILE_NAME = "Data File with name: {0} already existed";

	// FolderFileServiceManager
	public static final String MNG_NEW_FOLDER = "New Folder";
	public static final String MNG_EXC_EXISTED_FOLDER_NAME = "Folder: {0} already existed.";

	// ProjectFileServiceManager
	public static final String MNG_EXC_FAILED_TO_UPDATE_PROJ = "Failed to update project.";

	// TestCaseFileServiceManager
	public static final String MNG_NEW_TEST_CASE = "New Test Case";
	public static final String MNG_EXC_EXISTED_TEST_CASE_NAME_INSENSITVE = "Test case with name: {0} already existed. Case sensitive for test case is no longer supported.";

	// TestSuiteFileServiceManager
	public static final String MNG_NEW_TEST_SUITE = "New Test Suite";
	public static final String MNG_EXC_EXISTED_TEST_SUITE_NAME = "Test Suite with name: {0} already existed";

	// WebElementFileServiceManager
	public static final String MNG_NEW_ELEMENT = "New Element";
	public static final String MNG_NEW_REQUEST = "New Request";
}
