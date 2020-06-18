package com.kms.katalon.activation.plugin.constant;

import org.eclipse.osgi.util.NLS;

public class ActivationMessageConstants {
    private static final String BUNDLE_NAME = "com.kms.katalon.activation.plugin.constant.activationMessageConstants";
    
    public static String MSG_PLUGIN_HAS_BEEN_INSTALLED;

    public static String MSG_PLUGIN_HAS_BEEN_UNINSTALLED;

    public static String KStore_ERROR_INVALID_CREDENTAILS;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ActivationMessageConstants.class);
    }

    private ActivationMessageConstants() {
    }
}
