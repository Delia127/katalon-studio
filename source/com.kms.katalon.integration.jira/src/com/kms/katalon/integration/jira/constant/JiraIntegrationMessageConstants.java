package com.kms.katalon.integration.jira.constant;

import org.eclipse.osgi.util.NLS;

public class JiraIntegrationMessageConstants extends NLS {
    private static final String MESSAGE_FILE_NAME = StringConstants.JIRA_BUNDLE_ID + ".constant.JiraIntegrationMessage";
    static {
        // initialize resource bundle
        NLS.initializeMessages(MESSAGE_FILE_NAME, JiraIntegrationMessageConstants.class);
    }

    private JiraIntegrationMessageConstants() {
    }
    
    public static String MSG_INVALID_ACCOUNT;
    public static String MSG_INVALID_PERMISSION;
    public static String MSG_INVALID_REQUEST;
    public static String MSG_INVALID_SERVER_URL;
}
