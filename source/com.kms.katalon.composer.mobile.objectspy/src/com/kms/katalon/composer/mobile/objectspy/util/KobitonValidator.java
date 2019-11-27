package com.kms.katalon.composer.mobile.objectspy.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.handlers.PreferenceHandler;
import com.kms.katalon.composer.mobile.objectspy.constant.ComposerMobileObjectspyMessageConstants;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;

public class KobitonValidator {
    private static final String PREFERENCES_ID_SEPARATOR = "/";

    private static final int BUTTON_ID_GOTO_SETTINGS = 0;

    private static final String PREFERENCE_ID_KOBITON = "com.kms.katalon.composer.integration.kobiton.preferences";

    public static boolean validateKobitonIntergration() {
        if (!KobitonPreferencesProvider.isKobitonIntegrationAvailable()) {
            int result = new MessageDialog(Display.getCurrent().getActiveShell(), StringConstants.INFO, null,
                    ComposerMobileObjectspyMessageConstants.MSG_NEED_KOBITON_INTEGRATION_FOR_MOBILE,
                    MessageDialog.INFORMATION, new String[] {
                            ComposerMobileObjectspyMessageConstants.BTN_GO_TO_SETTINGS, IDialogConstants.CANCEL_LABEL },
                    0).open();
            switch (result) {
                case BUTTON_ID_GOTO_SETTINGS:
                    int preferenceResult = PreferenceHandler.doExecute(PreferenceHandler.DEFAULT_PREFERENCE_PAGE_ID
                            + PREFERENCES_ID_SEPARATOR + PREFERENCE_ID_KOBITON);
                    if (preferenceResult == Window.OK) {
                        return validateKobitonIntergration();
                    }
                    break;
            }
            return false;
        }
        return true;
    }
}
