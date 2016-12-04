package com.kms.katalon.composer.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.execution.constants.GenerateCommandPreferenceConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.console.ConsoleMain;
import com.kms.katalon.execution.entity.DefaultRerunSetting;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ComposerGenerateCommandPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    public static final String DEFAULT_SUITE_ID = "";

    public static final String DEFAULT_BROWSER = "";

    public static final String DEFAULT_REMOTE_WEB_DRIVER_TYPE = RemoteWebDriverConnectorType.Selenium.toString();

    public static final String DEFAULT_REMOTE_WEB_DRIVER_URL = "";

    public static final String DEFAULT_MOBILE_DEVICE = "";

    public static final String DEFAULT_CUSTOM_EXECUTION = "";

    public static final String DEFAULT_REPORT_OUTPUT_LOCATION = StringConstants.ROOT_FOLDER_NAME_REPORT;

    public static final boolean DEFAULT_REPORT_USE_RELATIVE_PATH = true;

    public static final String DEFAULT_REPORT_OUTPUT_NAME = StringConstants.DIA_TXT_DEFAULT_REPORT_NAME;

    public static final boolean DEFAULT_POST_EXECUTION_SEND_REPORT = false;

    public static final String DEFAULT_POST_EXECUTION_RECIPIENTS = "";

    public static final boolean DEFAULT_DISPLAY_CONSOLE_LOG = false;

    public static final boolean DEFAULT_NO_CLOSE_CONSOLE_LOG = false;

    public static final int DEFAULT_RETRY = DefaultRerunSetting.DEFAULT_RERUN_TIME;

    public static final boolean DEFAULT_RETRY_FOR_FAILED_TEST_CASES = false;

    public static final int DEFAULT_UPDATE_STATUS_TIME_INTERVAL = ConsoleMain.DEFAULT_SHOW_PROGRESS_DELAY;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(ComposerGenerateCommandPreferenceDefaultValueInitializer.class);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_SUITE_ID, DEFAULT_SUITE_ID);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_BROWSER, DEFAULT_BROWSER);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_REMOTE_WEB_DRIVER_TYPE,
                DEFAULT_REMOTE_WEB_DRIVER_TYPE);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_REMOTE_WEB_DRIVER_URL,
                DEFAULT_REMOTE_WEB_DRIVER_URL);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_MOBILE_DEVICE, DEFAULT_MOBILE_DEVICE);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_CUSTOM_EXECUTION, DEFAULT_CUSTOM_EXECUTION);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_REPORT_OUTPUT_LOCATION,
                DEFAULT_REPORT_OUTPUT_LOCATION);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_REPORT_USE_RELATIVE_PATH,
                DEFAULT_REPORT_USE_RELATIVE_PATH);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_REPORT_OUTPUT_NAME, DEFAULT_REPORT_OUTPUT_NAME);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_POST_EXECUTION_SEND_REPORT,
                DEFAULT_POST_EXECUTION_SEND_REPORT);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_POST_EXECUTION_RECIPIENTS,
                DEFAULT_POST_EXECUTION_RECIPIENTS);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_DISPLAY_CONSOLE_LOG,
                DEFAULT_DISPLAY_CONSOLE_LOG);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_NO_CLOSE_CONSOLE_LOG,
                DEFAULT_NO_CLOSE_CONSOLE_LOG);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY, DEFAULT_RETRY);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_RETRY_FOR_FAILED_TEST_CASES,
                DEFAULT_RETRY_FOR_FAILED_TEST_CASES);
        store.setDefault(GenerateCommandPreferenceConstants.GEN_COMMAND_UPDATE_STATUS_TIME_INTERVAL,
                DEFAULT_UPDATE_STATUS_TIME_INTERVAL);
    }

}
