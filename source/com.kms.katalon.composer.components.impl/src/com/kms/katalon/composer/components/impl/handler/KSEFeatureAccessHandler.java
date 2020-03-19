package com.kms.katalon.composer.components.impl.handler;

import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.dialogs.WarningKSEFeatureAccessDialog;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.feature.KSEFeature;
import com.kms.katalon.tracking.service.Trackings;

public class KSEFeatureAccessHandler {
    public static void handleUnauthorizedAccess(KSEFeature feature) {
        handleUnauthorizedAccess(feature, GlobalStringConstants.MSG_ACCESS_KSE_FEATURES_WARNING);
    }

    public static void handleUnauthorizedAccess(KSEFeature feature, String msg) {
        Trackings.trackUnauthorizedAccessOfKSEFeatures(feature);
        WarningKSEFeatureAccessDialog dialog = new WarningKSEFeatureAccessDialog(
                Display.getCurrent().getActiveShell(),
                msg);
        dialog.open();
    }
}