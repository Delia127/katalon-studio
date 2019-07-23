package com.kms.katalon.composer.integration.git.preference;

import java.io.IOException;

import com.kms.katalon.composer.integration.git.constants.GitPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class GitPreferenceUtil {
    public static boolean isGitEnabled() {
        return PreferenceStoreManager.getPreferenceStore(GitPreferenceUtil.class).getBoolean(
                GitPreferenceConstants.GIT_INTERGRATION_ENABLE);
    }
    
    public static void setEnable(boolean enabled) throws IOException {
        ScopedPreferenceStore store = PreferenceStoreManager.getPreferenceStore(GitPreferenceUtil.class);
        store.setValue(GitPreferenceConstants.GIT_INTERGRATION_ENABLE, enabled);
        store.save();
    }

}
