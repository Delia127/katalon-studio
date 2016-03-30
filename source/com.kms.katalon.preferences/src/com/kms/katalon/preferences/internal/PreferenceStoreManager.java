package com.kms.katalon.preferences.internal;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;

public class PreferenceStoreManager {
    private static LoadedPreferenceStore loadedPreferenceStore = LoadedPreferenceStore.getInstance();

    private static String getBundleQualifier(Class<?> clazzInBundle) {
        return FrameworkUtil.getBundle(clazzInBundle).getSymbolicName();
    }

    public static ScopedPreferenceStore getPreferenceStore(Class<?> clazzInBundle) {
        return getPreferenceStore(getBundleQualifier(clazzInBundle));
    }

    public static ScopedPreferenceStore getPreferenceStore(String qualifier) {
        if (loadedPreferenceStore.contains(qualifier)) {
            return loadedPreferenceStore.get(qualifier);
        }
        return initialPreferenceStore(qualifier);
    }

    private static ScopedPreferenceStore initialPreferenceStore(String qualifier) {
        ScopedPreferenceStore pref = new ScopedPreferenceStore(InstanceScope.INSTANCE, qualifier);
        loadedPreferenceStore.put(pref);
        return pref;
    }
}
