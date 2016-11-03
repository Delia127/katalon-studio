package com.kms.katalon.composer.integration.jira;

import com.kms.katalon.integration.jira.JiraIntegrationException;

public class JiraProgressResult {
    private JiraIntegrationException error;

    private boolean complete;

    public JiraIntegrationException getError() {
        return error;
    }

    public void setError(JiraIntegrationException error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
