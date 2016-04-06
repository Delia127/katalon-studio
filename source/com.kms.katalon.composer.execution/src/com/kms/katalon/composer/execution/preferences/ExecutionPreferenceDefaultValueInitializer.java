package com.kms.katalon.composer.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ExecutionPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final int EXECUTION_DEFAULT_TIMEOUT_VALUE = 30;

    public static final boolean EXECUTION_DEFAULT_IS_NOTIFY_ALLOWED_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_ALL_LOGS_VALUE = true;

    public static final boolean EXECUTION_DEFAULT_SHOW_INFO_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_PASSED_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_FAILED_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_ERROR_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_WARNING_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_LOGS_AS_TREE = false;

    public static final boolean EXECUTION_DEFAULT_PIN_LOG = false;

    public static final boolean EXECUTION_DEFAULT_ENABLE_WORD_WRAP = false;

    public static final String EXECUTION_DEFAULT_RUN_CONFIGURATION = "Firefox";

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(ExecutionPreferenceDefaultValueInitializer.class);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION,
                EXECUTION_DEFAULT_RUN_CONFIGURATION);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT, EXECUTION_DEFAULT_TIMEOUT_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_NOTIFY_AFTER_EXECUTING,
                EXECUTION_DEFAULT_IS_NOTIFY_ALLOWED_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);

        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS, EXECUTION_DEFAULT_SHOW_ALL_LOGS_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS, EXECUTION_DEFAULT_SHOW_INFO_LOGS_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS,
                EXECUTION_DEFAULT_SHOW_PASSED_LOGS_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS,
                EXECUTION_DEFAULT_SHOW_FAILED_LOGS_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS,
                EXECUTION_DEFAULT_SHOW_ERROR_LOGS_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_WARNING_LOGS,
                EXECUTION_DEFAULT_SHOW_WARNING_LOGS_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE, EXECUTION_DEFAULT_SHOW_LOGS_AS_TREE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_PIN_LOG, EXECUTION_DEFAULT_PIN_LOG);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP, EXECUTION_DEFAULT_ENABLE_WORD_WRAP);
    }
}
