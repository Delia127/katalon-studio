package com.kms.katalon.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.execution.constants.ExecutionPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ExecutionPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final boolean EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE = true;

    private static final boolean EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE = false;

    public static final int EXECUTION_DEFAULT_TIMEOUT_VALUE = 30;

    public static final boolean EXECUTION_DEFAULT_IS_NOTIFY_ALLOWED_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE = false;

    public static final String EXECUTION_DEFAULT_RUN_CONFIGURATION = "Firefox";

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(ExecutionPreferenceConstants.EXECUTION_QUALIFIER);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_DEFAULT_CONFIGURATION,
                EXECUTION_DEFAULT_RUN_CONFIGURATION);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_DEFAULT_TIMEOUT, EXECUTION_DEFAULT_TIMEOUT_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_NOTIFY_AFTER_EXECUTING,
                EXECUTION_DEFAULT_IS_NOTIFY_ALLOWED_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE,
                EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_CASE);
        store.setDefault(ExecutionPreferenceConstants.EXECUTION_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE,
                EXECUTION_DEFAULT_QUIT_DRIVERS_AFTER_EXECUTING_TEST_SUITE);
    }
}
