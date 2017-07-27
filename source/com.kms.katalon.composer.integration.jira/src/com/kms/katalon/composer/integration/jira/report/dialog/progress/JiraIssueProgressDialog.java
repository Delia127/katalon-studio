package com.kms.katalon.composer.integration.jira.report.dialog.progress;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.jira.JiraProgressDialog;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.jira.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraInvalidURLException;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.report.JiraReportService;

public abstract class JiraIssueProgressDialog extends JiraProgressDialog implements JiraUIComponent {
    protected TestCaseLogRecord logRecord;

    protected String issueKey;

    private JiraReportService reportService;

    public JiraIssueProgressDialog(Shell parent, String issueKey, TestCaseLogRecord logRecord) {
        super(parent);
        this.logRecord = logRecord;
        this.issueKey = issueKey;
        this.reportService = new JiraReportService();
    }

    protected void uploadTestCaseLog(TestCaseLogRecord logRecord, JiraIssue issue)
            throws IOException, JiraIntegrationException {
        reportService.uploadTestCaseLog(logRecord, issue);
    }

    protected void linkWithTestCaseJiraIssue(TestCaseLogRecord logRecord, JiraIssue issue)
            throws JiraIntegrationException, IOException {
        reportService.linkIssues(logRecord, issue);
    }

    protected void retrieveJiraIssue(JiraIntegrationAuthenticationHandler handler, JiraIssueProgressResult result)
            throws IOException, JiraIntegrationException {
        try {
            JiraIssue jiraIssue = handler.getJiraIssue(getCredential(), issueKey);
            result.setJiraIssue(jiraIssue);
        } catch (JiraInvalidURLException e) {
            throw new JiraIntegrationException(ComposerJiraIntegrationMessageConstant.JOB_MSG_INVALID_JIRA_ISSUE_KEY);
        }
    }
}
