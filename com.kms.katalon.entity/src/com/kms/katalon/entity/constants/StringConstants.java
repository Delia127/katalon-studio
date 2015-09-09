package com.kms.katalon.entity.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// DataFileNotFoundException
	public static final String EXC_NO_DATA_FILE_W_PROJ_ID_X_AND_GUID_Y = "There is no data file with project_id = {0} and guid = {1}";

	// DuplicatedDataFileNameException
	public static final String EXC_DUPLICATED_DATA_FILE_NAME = "Duplicated data file with name: {0} in parent folder id: {1}";

	// DuplicatedFileNameException
	public static final String EXC_DUPLICATED_FILE_NAME = "Duplicated file with name: {0} in parent folder id: {1}";

	// DuplicatedFolderException
	public static final String EXC_DUPLICATE_FOLDER_NAME = "Duplicated folder with name: {0} in parent folder id: {1}";

	// DuplicateEntityException
	public static final String EXC_DUPLICATE_ENTITY = "Duplicated Entity";

	// FilePathTooLongException
	public static final String EXC_CANNOT_SAVE_FILE_PATH_LENG_LIMIT_EXCEEDED = "Cannot save entity: file path length limit exceeded ({0}/{1}).";
	public static final String EXC_CANNOT_SAVE_CHILD_ENTITY_FILE_PATH_LIMIT_EXCEEDED = "Cannot save entity: child entity {0}''s file path length limit exceeded ({1}/{2}).";

	// LengthExceedLimitationException
	public static final String EXC_X_COULDNT_EXCEED_200_CHARS = "{0} couldn''t exceed 200 characters.";

	// MultipleEntitiesException
	public static final String EXC_MULTIPLE_ENTITIES_W_ID_X_AND_TYPE_Y = "There are multiple entities with id = {0} and EntityType: {1}";

	// NoEntityException
	public static final String EXC_NO_ENTITY_W_ID_X_AND_TYPE_Y = "There is no entity with id = {0} and EntityType: {1}";

	// ProjectVersionNotFoundException
	public static final String EXC_NO_PROJ_VER_W_ID_X_AND_VER_Y = "There is no project version with project_id = {0} and version = {1}";

	// TestCaseIsReferencedByTestSuiteExepception
	public static final String EXC_CANNOT_DEL_TEST_CASE_X_FOR_REASON = "Unable to delete test case \"{0}\" because this test case is referenced by the following test suite(s):\n{1}";

	// WebElementNotFoundException
	public static final String EXC_NO_WEB_ELEMENT_W_PROJ_ID_X_AND_GUID_Y = "There is no web element with project_id = {0} and guid = {1}";

	// WrongEntityVersionException
	public static final String EXC_WRONG_ENTITY_VER = "The {0} with id = {1} has been updated by someone else.";
}
