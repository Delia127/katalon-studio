package com.kms.katalon.composer.mobile.objectspy.dialog;

import com.kms.katalon.composer.mobile.objectspy.preferences.MobileObjectSpyPreferencesHelper;

/**
 * Interface for dialogs that control mobile apps
 *
 */
public interface MobileAppDialog {
    void updateDeviceNames();
    void refreshButtonsState();
    MobileObjectSpyPreferencesHelper getPreferencesHelper();
}
