package com.kms.katalon.preferences.internal;

import java.util.HashMap;
import java.util.Map;

public class LoadedPreferenceStore {
    private Map<String, ScopedPreferenceStore> prefs;

    private static LoadedPreferenceStore instance;

    public LoadedPreferenceStore() {
        this.prefs = new HashMap<String, ScopedPreferenceStore>();
    }

    public static LoadedPreferenceStore getInstance() {
        if (instance == null) {
            instance = new LoadedPreferenceStore();
        }
        return instance;
    }

    public boolean contains(String qualifier) {
        return prefs.containsKey(qualifier);
    }

    public void put(ScopedPreferenceStore pref) {
        prefs.put(pref.getQualifier(), pref);
    }

    public ScopedPreferenceStore get(String qualifier) {
        return prefs.get(qualifier);
    }
}
