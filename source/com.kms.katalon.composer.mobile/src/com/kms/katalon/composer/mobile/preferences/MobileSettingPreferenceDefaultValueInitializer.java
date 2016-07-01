package com.kms.katalon.composer.mobile.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.core.appium.constants.AppiumLogLevel;
import com.kms.katalon.execution.mobile.constants.MobilePreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class MobileSettingPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final String MOBILE_APPIUM_DIRECTORY_DEFAULT_VALUE = System.getenv("APPIUM_HOME");

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getPreferenceStore(MobilePreferenceConstants.MOBILE_QUALIFIER);
        store.setDefault(MobilePreferenceConstants.MOBILE_APPIUM_DIRECTORY,
                StringUtils.defaultString(MOBILE_APPIUM_DIRECTORY_DEFAULT_VALUE));
        store.setDefault(MobilePreferenceConstants.MOBILE_APPIUM_LOG_LEVEL, AppiumLogLevel.INFO);
    }

}
