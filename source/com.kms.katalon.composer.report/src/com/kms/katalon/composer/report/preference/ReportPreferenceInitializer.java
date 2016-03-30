package com.kms.katalon.composer.report.preference;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.composer.report.constants.ReportPreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ReportPreferenceInitializer extends AbstractPreferenceInitializer {

    private static ScopedPreferenceStore getStore() {
        return getPreferenceStore(ReportPreferenceInitializer.class);
    }

    @Override
    public void initializeDefaultPreferences() {
        getStore().setDefault(ReportPreferenceConstants.TEST_LOG_SEARCH_INCLUDE_CHILD_LOG_FOR_FIRST_MATCH, false);
    }

    public static boolean isChildLogForFirstMatchIncluded() {
        return getStore().getBoolean(ReportPreferenceConstants.TEST_LOG_SEARCH_INCLUDE_CHILD_LOG_FOR_FIRST_MATCH);
    }

    public static void includeChildLogForSearching(boolean included) throws IOException {
        ScopedPreferenceStore store = getStore();
        store.setValue(ReportPreferenceConstants.TEST_LOG_SEARCH_INCLUDE_CHILD_LOG_FOR_FIRST_MATCH, included);
        store.save();
    }
}
