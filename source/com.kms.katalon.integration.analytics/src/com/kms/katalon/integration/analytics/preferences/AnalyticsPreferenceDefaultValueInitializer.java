package com.kms.katalon.integration.analytics.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class AnalyticsPreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
//        getStore().setDefault(AnalyticsSettingStoreConstants.ANALYTICS_INTEGRATION_ENABLE, StringUtils.EMPTY);
//        getStore().setDefault(AnalyticsSettingStoreConstants.StringUtils.EMPTY,
//                DEFAULT_ANALYTICS_AUTHENTICATION_EMAIL);
//        getStore().setDefault(AnalyticsSettingStoreConstants.ANALYTICS_AUTHENTICATION_PASSWORD,
//                StringUtils.EMPTY);
//        getStore().setDefault(AnalyticsSettingStoreConstants.ANALYTICS_SERVER_ENDPOINT,
//                AnalyticsStringConstants.ANALYTICS_SERVER_TARGET_ENDPOINT);
    }
    
    public static ScopedPreferenceStore getStore() {
        return getPreferenceStore(AnalyticsPreferenceDefaultValueInitializer.class);
    }
}
