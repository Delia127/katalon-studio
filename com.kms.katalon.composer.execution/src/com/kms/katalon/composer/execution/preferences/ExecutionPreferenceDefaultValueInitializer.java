package com.kms.katalon.composer.execution.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.factory.BuiltinRunConfigurationFactory;

public class ExecutionPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
	public static final int EXECUTION_DEFAULT_TIMEOUT_VALUE = 30;
    public static final boolean EXECUTION_DEFAULT_IS_NOTIFY_ALLOWED_VALUE = false;
    public static final boolean EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE = false;

    public static final boolean EXECUTION_DEFAULT_SHOW_ALL_LOGS_VALUE = true;
    public static final boolean EXECUTION_DEFAULT_SHOW_INFO_LOGS_VALUE = false;
    public static final boolean EXECUTION_DEFAULT_SHOW_PASSED_LOGS_VALUE = false;
    public static final boolean EXECUTION_DEFAULT_SHOW_FAILED_LOGS_VALUE = false;
    public static final boolean EXECUTION_DEFAULT_SHOW_ERROR_LOGS_VALUE = false;
    public static final boolean EXECUTION_DEFAULT_SHOW_LOGS_AS_TREE = false;
    public static final boolean EXECUTION_DEFAULT_PIN_LOG = false;

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExecutionPreferenceConstans.QUALIFIER);
        IRunConfigurationContributor[] allBuiltinRunConfigurationContributor = BuiltinRunConfigurationFactory
                .getInstance().getAllRunConfigurationContributors();
        if (allBuiltinRunConfigurationContributor.length > 0) {
            store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_CONFIGURATION,
                    allBuiltinRunConfigurationContributor[0].getId());
        }
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_DEFAULT_TIMEOUT,
                        EXECUTION_DEFAULT_TIMEOUT_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_NOTIFY_AFTER_EXECUTING,
                EXECUTION_DEFAULT_IS_NOTIFY_ALLOWED_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_OPEN_REPORT_AFTER_EXECUTING,
                EXECUTION_DEFAULT_OPEN_REPORT_REPORT_VALUE);

        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_ALL_LOGS,
                EXECUTION_DEFAULT_SHOW_ALL_LOGS_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_INFO_LOGS,
                EXECUTION_DEFAULT_SHOW_INFO_LOGS_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_PASSED_LOGS,
                EXECUTION_DEFAULT_SHOW_PASSED_LOGS_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_FAILED_LOGS,
                EXECUTION_DEFAULT_SHOW_FAILED_LOGS_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_ERROR_LOGS,
                EXECUTION_DEFAULT_SHOW_ERROR_LOGS_VALUE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_SHOW_LOGS_AS_TREE, 
        		EXECUTION_DEFAULT_SHOW_LOGS_AS_TREE);
        store.setDefault(PreferenceConstants.ExecutionPreferenceConstans.EXECUTION_PIN_LOG, 
        		EXECUTION_DEFAULT_PIN_LOG);
    }
}
