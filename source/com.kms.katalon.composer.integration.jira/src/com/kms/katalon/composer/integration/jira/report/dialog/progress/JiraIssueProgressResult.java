package com.kms.katalon.composer.integration.jira.report.dialog.progress;

import com.kms.katalon.composer.integration.jira.JiraProgressResult;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class JiraIssueProgressResult extends JiraProgressResult {
    private JiraIssue jiraIssue;

    public JiraIssue getJiraIssue() {
        return jiraIssue;
    }

    public void setJiraIssue(JiraIssue jiraIssue) {
        this.jiraIssue = jiraIssue;
    }
}
