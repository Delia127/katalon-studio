package com.kms.katalon.composer.webservice.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerWebserviceMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.webservice.constants.composerWebserviceMessages";

    public static String HAND_NEW_REQUEST;

    public static String HAND_ERROR_MSG_UNABLE_TO_CREATE_NEW_REQ_OBJ;

    public static String MENU_CONTEXT_WEBSERVICE_REQ;

    public static String PA_TITLE_HTTP;

    public static String PA_LBL_HTTP_HEADER;

    public static String PA_LBL_HTTP_BODY;

    public static String VIEW_ERROR_MSG_FAILED_TO_LOAD_OBJ_REPOSITORY;

    public static String PA_TOOLTIP_DOUBLE_CLICK_FOR_QUICK_INSERT;

    public static String PA_TITLE_REST;

    public static String PA_LBL_URL;

    public static String PA_LBL_REQ_METHOD;

    public static String PA_LBL_REST_PARAMS;

    public static String PA_TITLE_SOAP;

    public static String PA_LBL_SERVICE_FUNCTION;

    public static String PA_LBL_PARAMS;

    public static String PA_LBL_SOAP_HEADER_OPT;

    public static String PA_LBL_SOAP_BODY;

    public static String WIEW_TITLE_NEW;

    public static String VIEW_LBL_REQ_TYPE;

    public static String VIEW_DIA_TITLE_WEBSERVICE_REQ;

    public static String VIEW_DIA_MSG_CREATE_NEW_WEBSERVICE_REQ;

    public static String VIEW_MENU_CONTEXT_INSERT_PROP;

    public static String VIEW_MENU_CONTEXT_DEL_PROPS;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerWebserviceMessageConstants.class);
    }

    private ComposerWebserviceMessageConstants() {
    }
}
