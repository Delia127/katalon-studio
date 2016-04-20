package com.kms.katalon.composer.testcase.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import static com.kms.katalon.composer.testcase.constants.TestCasePreferenceConstants.MANUAL_MAXIMUM_LINE_WIDTH;
import static com.kms.katalon.composer.testcase.constants.TestCasePreferenceConstants.MANUAL_ALLOW_LINE_WRAPPING;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ManualPreferenceValueInitializer extends AbstractPreferenceInitializer {

    static ScopedPreferenceStore getStore() {
        return getPreferenceStore(ManualPreferenceValueInitializer.class);
    }

    @Override
    public void initializeDefaultPreferences() {
        // TODO Auto-generated method stub
        ScopedPreferenceStore store = getStore();

        // Default value type of variable
        store.setDefault(MANUAL_ALLOW_LINE_WRAPPING, true);

        // Test Case Calling
        store.setDefault(MANUAL_MAXIMUM_LINE_WIDTH, 120);
    }

    public static boolean isLineWrappingEnabled() {
        return getStore().getBoolean(MANUAL_ALLOW_LINE_WRAPPING);
    }

    public static void enableLineWrapping(boolean enabled) {
        getStore().setValue(MANUAL_ALLOW_LINE_WRAPPING, enabled);
    }

    public static int getMaximumLineWidth() {
        return getStore().getInt(MANUAL_MAXIMUM_LINE_WIDTH);
    }

    public static void setMaximumLineWidth(int width) {
        getStore().setValue(MANUAL_MAXIMUM_LINE_WIDTH, width);
    }
    
    public static void updateStore() throws IOException {
        getStore().save();
    }

    public static void defaultStore() {
        ScopedPreferenceStore store = getStore();
        store.setValue(MANUAL_ALLOW_LINE_WRAPPING, store.getDefaultBoolean(MANUAL_ALLOW_LINE_WRAPPING));
        store.setValue(MANUAL_MAXIMUM_LINE_WIDTH, store.getDefaultInt(MANUAL_MAXIMUM_LINE_WIDTH));
    }
}
