package com.kms.katalon.composer.integration.kobiton.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerIntegrationKobitonMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.integration.kobiton.constants.composerIntegrationKobitonMessages"; //$NON-NLS-1$

    public static String BTN_CONNECT;

    public static String JOB_LOADING_DEVICE_LIST;

    public static String KobitonPreferencesPage_WARN_MSG_NO_API_KEY;

    public static String LBL_API_KEYS;

    public static String LBL_AUTHENTICATE_GROUP;

    public static String LBL_DLG_AUTHENTICATE_PASSWORD;

    public static String LBL_DLG_AUTHENTICATE_USERNAME;

    public static String LBL_DLG_DEVICE_NAME;

    public static String LBL_ENABLE_KOBITON_INTEGRATION;

    public static String LBL_LINK_DLG_AUTHENTICATE_FORGOT_PASSWORD;

    public static String LBL_LINK_DLG_AUTHENTICATE_REGISTER;

    public static String LBL_MENU_EXECUTION_KOBITON;

    public static String LBL_PASSWORD;

    public static String LBL_SERVER_URL;

    public static String LBL_USERNAME;

    public static String LNK_DLG_UPDATE_FAVORITE_DEVICES;

    public static String MSG_DLG_AUTHENTICATE_LOGIN_TO_KOBITON;

    public static String MSG_DLG_FAVORITE_DEVICES;

    public static String MSG_DLG_PRG_CONNECTING_TO_SERVER;

    public static String MSG_DLG_PRG_GETTING_KEYS;

    public static String MSG_DLG_PRG_RETRIEVING_KEYS;

    public static String MSG_DLG_PRG_SUCCESSFULLY;

    public static String MSG_INFO_DLG_AUTHENTICATE_ENTER_USERNAME_PASSWORD;

    public static String TITLE_DLG_AUTHENTICATE;

    public static String TITLE_DLG_FAVORITE_DEVICES;

    public static String TITLE_WINDOW_DLG_FAVORITE_DEVICES;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerIntegrationKobitonMessageConstants.class);
    }

    private ComposerIntegrationKobitonMessageConstants() {
    }
}
