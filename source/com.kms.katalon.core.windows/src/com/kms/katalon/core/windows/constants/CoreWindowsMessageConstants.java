package com.kms.katalon.core.windows.constants;

import org.eclipse.osgi.util.NLS;

public class CoreWindowsMessageConstants extends NLS {
    
    private static final String BUNDLE_NAME = "com.kms.katalon.core.windows.constants.coreWindowsMessages";
    
    public static String COMM_WINDOWS_HAS_NOT_STARTED;

    public static String KW_ENCRYPTED_TEXT_IS_NULL;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, CoreWindowsMessageConstants.class);
    }

    private CoreWindowsMessageConstants() {
    }
}
