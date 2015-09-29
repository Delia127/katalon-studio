package com.kms.katalon.core.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.constants.StringConstants;

public class RunConfiguration {
    public static final String LOG_FILE_PATH_PROPERTY = StringConstants.CONF_PROPERTY_LOG_FILE_PATH;
    public static final String TIMEOUT_PROPERTY = StringConstants.CONF_PROPERTY_TIMEOUT;
    public static final String PROJECT_DIR_PROPERTY = StringConstants.CONF_PROPERTY_PROJECT_DIR;

    public static final String HOST_NAME = StringConstants.CONF_PROPERTY_HOST_NAME;
    public static final String HOST_OS = StringConstants.CONF_PROPERTY_HOST_OS;
    public static final String EXCUTION_SOURCE = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE;
    public static final String EXCUTION_SOURCE_NAME = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE_NAME;
    public static final String EXCUTION_SOURCE_ID = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE_ID;
    public static final String EXCUTION_SOURCE_DESCRIPTION = StringConstants.CONF_PROPERTY_EXECUTION_SOURCE_DESCRIPTION;
    public static final String EXECUTION_DRIVER_PROPERTY = StringConstants.CONF_PROPERTY_EXECUTION_DRIVER_PROPERTY;

    private static String logFilePath = "";

    private static Map<String, Object> executionSettingMap = new HashMap<String, Object>();

    public static void setLogFile(String logFilePath) {
        if (logFilePath == null) {
            return;
        }
        RunConfiguration.logFilePath = logFilePath;
    }

    public static void setExecutionSettingFile(String executionSettingFilePath) {
        if (executionSettingFilePath == null) {
            return;
        }
        File executionSettingFile = new File(executionSettingFilePath);
        if (executionSettingFile.exists() && executionSettingFile.isFile()) {
            Gson gsonObj = new Gson();
            try {
                String propertyConfigFileContent = FileUtils.readFileToString(executionSettingFile);
                Type collectionType = new TypeToken<Map<String, Object>>() {
                }.getType();
                Map<String, Object> result = gsonObj.fromJson(propertyConfigFileContent, collectionType);
                if (result != null) {
                    executionSettingMap = result;
                }
            } catch (IOException | JsonSyntaxException exception) {
                // reading file failed or parsing json failed --> do nothing;
            }
        }
    }
    
    public static Object getProperty(String propertyKey) {
        return executionSettingMap.get(propertyKey);
    }
    
    public static String getStringProperty(String propertyKey) {
        return String.valueOf(executionSettingMap.get(propertyKey));
    }

    public static String getLogFilePath() {
        return logFilePath;
    }

    public static int getTimeOut() {
        return Integer.parseInt(getStringProperty(TIMEOUT_PROPERTY));
    }

    public static String getProjectDir() {
        return getStringProperty(PROJECT_DIR_PROPERTY);
    }

    public static String getHostName() {
        return getStringProperty(HOST_NAME);
    }

    public static String getOS() {
        return getStringProperty(HOST_OS);
    }

    public static String getExecutionSource() {
        return getStringProperty(EXCUTION_SOURCE);
    }

    public static String getExecutionSourceName() {
        return getStringProperty(EXCUTION_SOURCE_NAME);
    }

    public static String getExecutionSourceId() {
        return getStringProperty(EXCUTION_SOURCE_ID);
    }

    public static String getExecutionSourceDescription() {
        return getStringProperty(EXCUTION_SOURCE_DESCRIPTION);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getExecutionDriverProperty() {
        return (Map<String, Object>) getProperty(EXECUTION_DRIVER_PROPERTY);
    }

}
