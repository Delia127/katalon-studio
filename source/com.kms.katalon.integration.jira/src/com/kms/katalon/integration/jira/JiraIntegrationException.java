package com.kms.katalon.integration.jira;

public class JiraIntegrationException extends Exception {
    private static final long serialVersionUID = -2603412343572726289L;

    public JiraIntegrationException(String message) {
        super(message);
    }

    public JiraIntegrationException(Exception ex) {
        super(ex);
    }
}
