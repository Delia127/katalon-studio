package com.kms.katalon.composer.integration.qtest.activation.constant;

import org.eclipse.osgi.util.NLS;


public class QTestActivationMessageConstant {
    private static String BUNDLE_NAME = "com.kms.katalon.composer.integration.qtest.activation.constant.qTestActivationMessageConstantMessage";
    
    public static String NoLongerSupportFreePackageDialog_WARN_MSG_NO_LONGER_SUPPORT;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, QTestActivationMessageConstant.class);
    }

    private QTestActivationMessageConstant() {
    }
}
