package com.kms.katalon.composer.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.execution.constants.ComposerExecutionPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class ComposerExecutionPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    public static final boolean EXECUTION_DEFAULT_SHOW_ALL_LOGS_VALUE = true;

    public static final boolean EXECUTION_DEFAULT_SHOW_INFO_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_PASSED_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_FAILED_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_ERROR_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_WARNING_LOGS_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_LOGS_AS_TREE = false;

    public static final boolean EXECUTION_DEFAULT_PIN_LOG = false;

    public static final boolean EXECUTION_DEFAULT_ENABLE_WORD_WRAP = false;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = PreferenceStoreManager
                .getPreferenceStore(ComposerExecutionPreferenceDefaultValueInitializer.class);

        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_ALL_LOGS, EXECUTION_DEFAULT_SHOW_ALL_LOGS_VALUE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_INFO_LOGS, EXECUTION_DEFAULT_SHOW_INFO_LOGS_VALUE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_PASSED_LOGS,
                EXECUTION_DEFAULT_SHOW_PASSED_LOGS_VALUE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_FAILED_LOGS,
                EXECUTION_DEFAULT_SHOW_FAILED_LOGS_VALUE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_ERROR_LOGS,
                EXECUTION_DEFAULT_SHOW_ERROR_LOGS_VALUE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_WARNING_LOGS,
                EXECUTION_DEFAULT_SHOW_WARNING_LOGS_VALUE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_SHOW_LOGS_AS_TREE, EXECUTION_DEFAULT_SHOW_LOGS_AS_TREE);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_PIN_LOG, EXECUTION_DEFAULT_PIN_LOG);
        store.setDefault(ComposerExecutionPreferenceConstants.EXECUTION_ENABLE_WORD_WRAP, EXECUTION_DEFAULT_ENABLE_WORD_WRAP);
    }
}
