package com.kms.katalon.execution.setting;

import java.io.IOException;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.logging.LogUtil;

public class TestCaseSettingStore extends BundleSettingStore {

    private static final FailureHandling DEFAULT_FAILURE_HANDLING_IF_NOT_SET = FailureHandling.STOP_ON_FAILURE;

    public TestCaseSettingStore(String projectDir) {
        super(projectDir, StringConstants.TESTCASE_SETTINGS_FILE_NAME, false);
    }

    public FailureHandling getDefaultFailureHandling() {
        try {
            return FailureHandling.valueOf(getString(StringConstants.CONF_PROPERTY_DEFAULT_FAILURE_HANDLING,
                    DEFAULT_FAILURE_HANDLING_IF_NOT_SET.name()));
        } catch (NullPointerException | IllegalArgumentException | IOException e) {
            return DEFAULT_FAILURE_HANDLING_IF_NOT_SET;
        }
    }

    public void saveDefaultFailureHandling(String value) {
        try {
            setProperty(StringConstants.CONF_PROPERTY_DEFAULT_FAILURE_HANDLING, value);
        } catch (IOException | IllegalArgumentException e) {
            LogUtil.logError(e);
        }
    }
}
