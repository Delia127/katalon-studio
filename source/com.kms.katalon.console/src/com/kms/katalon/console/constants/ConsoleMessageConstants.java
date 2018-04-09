package com.kms.katalon.console.constants;

import org.eclipse.osgi.util.NLS;

public class ConsoleMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.console.constants.consoleMessages"; //$NON-NLS-1$

    public static String ERR_CONSOLE_MODE;

    public static String KATALON_NOT_ACTIVATED;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ConsoleMessageConstants.class);
    }

    private ConsoleMessageConstants() {
    }
}
