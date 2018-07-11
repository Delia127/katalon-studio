package com.kms.katalon.util;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.File;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.composer.preferences.GeneralPreferenceDefaultValueInitializer;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ApplicationSession {

    public static void clean() {
        ScopedPreferenceStore prefStore = getStore();
        if (prefStore == null) {
            return;
        }

        try {
            if (prefStore.getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION)
                    && !prefStore.getBoolean(PreferenceConstants.GENERAL_APP_CLOSE_SUDDENLY)) {
                return;
            }

            // Clear workbench layout
            File workbenchXmi = new File(Platform.getLocation().toString()
                    + "/.metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi");
            if (workbenchXmi.exists()) {
                workbenchXmi.delete();
            }

            // Clear working state of recent projects
            ProjectController.getInstance().clearWorkingStateOfRecentProjects();
        } finally {
            prefStore.setValue(PreferenceConstants.GENERAL_APP_CLOSE_SUDDENLY, true);
        }
    }

    public static void close() {
        ScopedPreferenceStore prefStore = getStore();
        if (prefStore == null) {
            return;
        }
        prefStore.setValue(PreferenceConstants.GENERAL_APP_CLOSE_SUDDENLY, false);
    }

    public static ScopedPreferenceStore getStore() {
        return getPreferenceStore(GeneralPreferenceDefaultValueInitializer.class);
    }

}
