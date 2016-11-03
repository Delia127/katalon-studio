package com.kms.katalon.integration.jira.issue;

import java.text.MessageFormat;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.integration.jira.constant.JiraIntegrationMessageConstants;
import com.kms.katalon.integration.jira.entity.JiraEdittingIssue;
import com.kms.katalon.integration.jira.entity.JiraIssue;

public class IssueMetaDataProvider {

    protected TestCaseLogRecord logRecord;

    public IssueMetaDataProvider(TestCaseLogRecord logRecord) {
        this.logRecord = logRecord;
    }

    public String getDescription() {
        return getStepDescription() + getErrorMessage();
    }

    /**
     * @return description of steps on JIRA looks like this:
     * 
     * <pre>
     * 1. openBrowser
     * 2. navigateToURL
     * </pre>
     */
    private String getStepDescription() {
        StringBuilder builder = new StringBuilder();
        ILogRecord[] childRecords = logRecord.getChildRecords();
        if (childRecords.length > 0) {
            builder.append("Test Steps:\n");
            for (int i = 0; i < childRecords.length; i++) {
                ILogRecord logRecord = childRecords[i];
                builder.append(Integer.toString(i + 1)).append(". ").append(
                        StringUtils.defaultString(logRecord.getName()))
                .append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private String getErrorMessage() {
        if (!logRecord.getStatus().getStatusValue().isError()) {
            return StringUtils.EMPTY;
        }
        String message = logRecord.getMessage();
        if (StringUtils.isEmpty(message)) {
            return StringUtils.EMPTY;
        }
        return MessageFormat.format(JiraIntegrationMessageConstants.MSG_ERROR_LOG, message);
    }

    public String getSummary() {
        String testCaseId = logRecord.getName();
        return testCaseId.substring(testCaseId.lastIndexOf("/") + 1, testCaseId.length());
    }

    public String getEnvironment() {
        StringBuilder builder = new StringBuilder();
        TestSuiteLogRecord testSuiteLogRecord = (TestSuiteLogRecord) logRecord.getParentLogRecord();
        for (Entry<String, String> runDataEntry : testSuiteLogRecord.getRunData().entrySet()) {
            builder.append("- ")
                    .append(runDataEntry.getKey())
                    .append(": ")
                    .append(runDataEntry.getValue())
                    .append("\n");
        }
        return builder.toString();
    }

    public JiraEdittingIssue toEdittingIssue(JiraIssue jiraIssue) {
        return new JiraEdittingIssue(updateDescriptionForJiraIssue(jiraIssue));
    }

    private String updateDescriptionForJiraIssue(JiraIssue jiraIssue) {
        StringBuilder descriptionBuilder = new StringBuilder();

        String oldDescription = jiraIssue.getFields().getDescription();
        if (StringUtils.isNotEmpty(oldDescription)) {
            descriptionBuilder.append(oldDescription).append("\n");
        }
        descriptionBuilder.append(getDescription());
        return descriptionBuilder.toString();
    }
}
