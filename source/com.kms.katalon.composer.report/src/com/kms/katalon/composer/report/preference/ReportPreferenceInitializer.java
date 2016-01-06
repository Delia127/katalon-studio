package com.kms.katalon.composer.report.preference;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ReportPreferenceInitializer extends AbstractPreferenceInitializer {

    private static ScopedPreferenceStore getStore() {
        return new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ReportPreferenceConstants.QUALIFIER);
    }

    @Override
    public void initializeDefaultPreferences() {
        getStore().setDefault(
                PreferenceConstants.ReportPreferenceConstants.TEST_LOG_SEARCH_INCLUDE_CHILD_LOG_FOR_FIRST_MATCH, false);
    }

    public static boolean isChildLogForFirstMatchIncluded() {
        return getStore().getBoolean(
                PreferenceConstants.ReportPreferenceConstants.TEST_LOG_SEARCH_INCLUDE_CHILD_LOG_FOR_FIRST_MATCH);
    }

    public static void includeChildLogForSearching(boolean included) throws IOException {
        ScopedPreferenceStore store = getStore();
        store.setValue(PreferenceConstants.ReportPreferenceConstants.TEST_LOG_SEARCH_INCLUDE_CHILD_LOG_FOR_FIRST_MATCH,
                included);
        store.save();
    }
}
