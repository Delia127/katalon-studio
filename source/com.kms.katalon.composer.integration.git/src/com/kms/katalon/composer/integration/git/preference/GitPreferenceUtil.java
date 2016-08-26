package com.kms.katalon.composer.integration.git.preference;

import com.kms.katalon.composer.integration.git.constants.GitPreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class GitPreferenceUtil {
    public static boolean isGitEnabled() {
        return PreferenceStoreManager.getPreferenceStore(GitPreferenceUtil.class).getBoolean(
                GitPreferenceConstants.GIT_INTERGRATION_ENABLE);
    }

}
