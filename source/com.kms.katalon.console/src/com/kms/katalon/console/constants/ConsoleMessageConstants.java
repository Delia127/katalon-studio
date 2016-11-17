package com.kms.katalon.console.constants;

import org.eclipse.osgi.util.NLS;

public class ConsoleMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.console.constants.consoleMessages"; //$NON-NLS-1$

    public static String ACTIVATE_INFO_INVALID;

    public static String ACTIVATION_CODE_INVALID;

    public static String ACTIVATION_COLLECT_FAIL_MESSAGE;

    public static String ERR_CONSOLE_MODE;

    public static String KATALON_NOT_ACTIVATED;

    public static String NETWORK_ERROR;

    public static String SEND_SUCCESS_RESPONSE;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ConsoleMessageConstants.class);
    }

    private ConsoleMessageConstants() {
    }
}
