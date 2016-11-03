package com.kms.katalon.composer.integration.jira.report.dialog.progress;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.jira.JiraProgressDialog;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.integration.jira.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraInvalidURLException;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public abstract class JiraIssueProgressDialog extends JiraProgressDialog implements JiraUIComponent {
    protected TestCaseLogRecord logRecord;

    protected String issueKey;

    public JiraIssueProgressDialog(Shell parent, String issueKey, TestCaseLogRecord logRecord) {
        super(parent);
        this.logRecord = logRecord;
        this.issueKey = issueKey;
    }

    protected void uploadTestCaseLog(JiraIntegrationAuthenticationHandler handler, JiraIssueProgressResult result)
            throws IOException, JiraIntegrationException {
        TestSuiteLogRecord testSuiteLogRecord = (TestSuiteLogRecord) logRecord.getParentLogRecord();

        String logFolder = testSuiteLogRecord.getLogFolder();
        if (getSettingStore().isAttachScreenshotEnabled()) {
            for (String screenshot : logRecord.getAttachments()) {
                handler.uploadAttachment(getCredential(), result.getJiraIssue(),
                        new File(logFolder, screenshot).getAbsolutePath());
            }
        }

        if (getSettingStore().isAttachLogEnabled()) {
            for (String logFile : testSuiteLogRecord.getLogFiles()) {
                handler.uploadAttachment(getCredential(), result.getJiraIssue(),
                        new File(logFolder, logFile).getAbsolutePath());
            }
        }
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
