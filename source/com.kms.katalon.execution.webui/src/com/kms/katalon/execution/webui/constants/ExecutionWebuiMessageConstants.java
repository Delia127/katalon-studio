package com.kms.katalon.execution.webui.constants;

import org.eclipse.osgi.util.NLS;

public class ExecutionWebuiMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.execution.webui.constants.executionWebuiMessages";

    public static String REMOTE_WEB_DRIVER_ERR_NO_URL_AVAILABLE;

    public static String REMOTE_WEB_DRIVER_ERR_NO_TYPE_AVAILABLE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ExecutionWebuiMessageConstants.class);
    }

    private ExecutionWebuiMessageConstants() {
    }
}
