package com.kms.katalon.composer.testcase.preferences;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;

public class TestCaseSettingStore {

    private static final FailureHandling DEFAULT_FAILURE_HANDLING_IF_NOT_SET = FailureHandling.STOP_ON_FAILURE;

    private static File getPropertyFile(String projectDir) throws IOException {
        File configFile = new File(projectDir + File.separator
                + PropertySettingStoreUtil.INTERNAL_SETTING_ROOT_FOLDER_NAME + File.separator
                + StringConstants.TESTCASE_SETTINGS_FILE_NAME + PropertySettingStoreUtil.PROPERTY_FILE_EXENSION);
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        return configFile;
    }

    public static FailureHandling getDefaultFailureHandling(String projectDir) {
        try {
            return FailureHandling.valueOf(PropertySettingStoreUtil.getPropertyValue(
                    StringConstants.CONF_PROPERTY_DEFAULT_FAILURE_HANDLING, getPropertyFile(projectDir)));
        } catch (NullPointerException | IllegalArgumentException | IOException e) {
            return DEFAULT_FAILURE_HANDLING_IF_NOT_SET;
        }
    }

    public static void saveDefaultFailureHandling(String projectDir, String value) {
        try {
            if (projectDir != null) {
                PropertySettingStoreUtil.addNewProperty(StringConstants.CONF_PROPERTY_DEFAULT_FAILURE_HANDLING, value,
                        getPropertyFile(projectDir));
            }
        } catch (IOException | IllegalArgumentException e) {
            LoggerSingleton.logError(e);
        }
    }
}
