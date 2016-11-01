package com.kms.katalon.integration.jira;

import java.io.IOException;

public class JiraIntegrationException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = -2603412343572726289L;
    public JiraIntegrationException(String message) {
        super(message);
    }
    
    public JiraIntegrationException(IOException ex) {
        super(ex);
    }
}
