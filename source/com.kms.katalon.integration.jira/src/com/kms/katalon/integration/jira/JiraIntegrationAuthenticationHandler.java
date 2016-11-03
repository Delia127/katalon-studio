package com.kms.katalon.integration.jira;

import com.atlassian.jira.rest.client.api.domain.User;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.util.JsonUtil;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.entity.JiraIssueType;
import com.kms.katalon.integration.jira.entity.JiraProject;
import com.kms.katalon.integration.jira.issue.IssueMetaDataProvider;
import com.kms.katalon.integration.jira.request.JiraIntegrationRequest;

public class JiraIntegrationAuthenticationHandler extends JiraIntegrationRequest {

    public User authenticate(JiraCredential credential) throws JiraIntegrationException {
        return getJiraObject(credential, JiraAPIURL.getUserAPIUrl(credential), User.class);
    }

    public JiraIssue getJiraIssue(JiraCredential credential, String issueKey) throws JiraIntegrationException {
        return getJiraObject(credential, JiraAPIURL.getIssueAPIUrl(credential) + "/" + issueKey, JiraIssue.class);
    }

    public JiraIssueType[] getJiraIssuesTypes(JiraCredential credential) throws JiraIntegrationException {
        return getJiraArrayObjects(credential, JiraAPIURL.getIssueTypeAPIUrl(credential), JiraIssueType[].class);
    }

    public JiraProject[] getJiraProjects(JiraCredential credential) throws JiraIntegrationException {
        return getJiraArrayObjects(credential, JiraAPIURL.getProjectAPIUrl(credential), JiraProject[].class);
    }

    public void uploadAttachment(JiraCredential credential, JiraIssue issue, String logFilePath)
            throws JiraIntegrationException {
        sendUploadRequest(credential, JiraAPIURL.getIssueaAttachmentsAPIUrl(credential, issue.getKey()), logFilePath);
    }

    public void updateIssue(JiraCredential credential, JiraIssue jiraIssue, TestCaseLogRecord logRecord)
            throws JiraIntegrationException {
        IssueMetaDataProvider metaDataProvider = new IssueMetaDataProvider(logRecord);
        sendPutRequest(credential, JiraAPIURL.getIssueAPIUrl(credential) + "/" + jiraIssue.getKey(),
                JsonUtil.toJson(metaDataProvider.toEdittingIssue(jiraIssue), false));
    }
}
