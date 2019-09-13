package com.kms.katalon.dal.fileservice;

import java.io.File;

import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class FileServiceConstant {
    public static final String TEST_CASE_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_TEST_CASE;

    public static final String TEST_SUITE_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_TEST_SUITE;

    public static final String DATA_FILE_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_DATA_FILE;

    public static final String OBJECT_REPOSITORY_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_OBJECT_REPOSITORY;

    public static final String KEYWORD_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_KEYWORD;

    private static final String GLOBAL_VARIABLE_FILE_NAME = StringConstants.FS_FILE_NAME_GLOBAL_VARIABLE;

    public static final String REPORT_ROOT_FOLDER_NAME = StringConstants.FS_ROOT_FOLDER_NAME_REPORT;

    public static final String CHECKPOINT_ROOT_FOLDER_NAME = StringConstants.ROOT_FOLDER_NAME_CHECKPOINT;
    
    public static final String INCLUDE_SCRIPT_ROOT_FOLDER_NAME = GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE;
    
    public static final String GROOVY_SCRIPTS_INCLUDE_FOLDER = GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE + "/scripts/groovy";
    
    public static final String FEATURES_INCLUDE_FOLDER = GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE + "/" + GlobalMessageConstants.ROOT_FOLDER_NAME_FEATURES;

    public static final String CONFIG_INCLUDE_FOLDER = GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE + "/" + GlobalMessageConstants.ROOT_FOLDER_NAME_CONFIG;
    
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "Katalon";

    public static final String SETTING_DIR = "settings";

    public static final String EXTERNAL_SETTING_DIR = SETTING_DIR + File.separator + "external";

    public static final String INTERNAL_SETTING_DIR = SETTING_DIR + File.separator + "internal";
    
    public static final String REPORT_COLLECTION_FILE_EXTENSION = ".rp";

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

    public static String getCheckpointRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + CHECKPOINT_ROOT_FOLDER_NAME;
    }

    public static String getTestListenerRootFolderLocation(String projectFolder) {
        return projectFolder + File.separator + GlobalMessageConstants.ROOT_FOLDER_NAME_TEST_LISTENER;
    }

    public static String getLegacyGlobalVariableFileLocation(String projectFolder) {
        return projectFolder + File.separator + GLOBAL_VARIABLE_FILE_NAME
                + ExecutionProfileEntity.getGlobalVariableFileExtension();
    }

    public static String getFeatureFolderLocation(String projectFolder) {
        return projectFolder + File.separator + FEATURES_INCLUDE_FOLDER;
    }
    
    public static String getConfigFolderLocation(String projectFolder) {
        return projectFolder + File.separator + CONFIG_INCLUDE_FOLDER;
    }

    public static String getSourceFolderLocation(String projectFolder) {
        return projectFolder + File.separator + GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE;
    }

    public static String getGroovyScriptFolderLocation(String projectFolder) {
        return projectFolder + File.separator + GROOVY_SCRIPTS_INCLUDE_FOLDER;
    }

    public static String getProfileFolderLocation(String projectFolder) {
        return projectFolder + File.separator + "Profiles";
    }
    
    public static String getLocation(String projectFolder) {
        return projectFolder + File.separator + GlobalMessageConstants.ROOT_FOLDER_NAME_INCLUDE;
    }
    
    public static String getPluginFolderLocation(String projectFolder) {
        return projectFolder + File.separator + GlobalMessageConstants.SYSTEM_FOLDER_NAME_PLUGIN;
    }
}
