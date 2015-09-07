package com.kms.katalon.dal.fileservice;

import java.io.File;

import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.entity.GlobalVariableWrapper;

public class FileServiceConstant {
	public static final String TEST_CASE_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_TEST_CASE;
	
	public static final String TEST_SUITE_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_TEST_SUITE;
	
	public static final String DATA_FILE_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_DATA_FILE;
	
	public static final String OBJECT_REPOSITORY_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_OBJECT_REPOSITORY;
	
	public static final String KEYWORD_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_KEYWORD;
	
    private static final String GLOBAL_VARIABLE_FILE_NAME = StringConstants.FS_FILE_NAME_GLOBAL_VARIABLE;
    
    public static final String REPORT_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_REPORT;
    
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "Katalon";
    
    public static final String SETTING_DIR = "settings";
    
    public static final String EXTERNAL_SETTING_DIR = SETTING_DIR + File.separator + "external";
    
    public static final String INTERNAL_SETTING_DIR = SETTING_DIR + File.separator + "internal";
    
    public static final int MAX_FILE_PATH_LENGTH = 255;
    
    public static String getTestCaseRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + TEST_CASE_ROOT_FOLDER_NAME;
    }
    
    public static String getTestSuiteRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + TEST_SUITE_ROOT_FOLDER_NAME;
    }
    
    public static String getDataFileRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + DATA_FILE_ROOT_FOLDER_NAME;
    }
    
    public static String getObjectRepositoryRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + OBJECT_REPOSITORY_ROOT_FOLDER_NAME;
    }
    
    public static String getReportRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + REPORT_ROOT_FOLDER_NAME;
    }
    
    public static String getKeywordRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + KEYWORD_ROOT_FOLDER_NAME;
    }
    
    public static String getGlobalVariableFileLocation(String projectFolder) {
		return projectFolder + File.separator + GLOBAL_VARIABLE_FILE_NAME
				+ GlobalVariableWrapper.getGlobalVariableFileExtension();
    }
}
