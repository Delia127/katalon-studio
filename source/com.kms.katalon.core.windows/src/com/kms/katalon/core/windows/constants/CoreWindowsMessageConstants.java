package com.kms.katalon.core.windows.constants;

import org.eclipse.osgi.util.NLS;

public class CoreWindowsMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.core.windows.constants.coreWindowsMessages";
    
    public static String WindowsActionHelper_INFO_START_FINDING_WINDOW;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, CoreWindowsMessageConstants.class);
    }

    private CoreWindowsMessageConstants() {
    }
}
