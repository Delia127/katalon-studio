package com.kms.katalon.composer.integration.jira;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.jira.JiraComponent;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;

public interface JiraUIComponent extends JiraComponent {
    default String getHTMLIssueURLPrefix() throws IOException {
        return getSettingStore().getServerUrl() + StringConstants.HREF_BROWSE_ISSUE;
    }

    default URI getHTMLLink(JiraIssue jiraIssue) throws URISyntaxException, IOException {
        return new URI(getHTMLIssueURLPrefix() + "/" + jiraIssue.getKey());
    }

    default int getTestCaseLogRecordIndex(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
        return LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity).getChildIndex(logRecord);
    }

    default JiraIssueCollection getJiraIssueCollection(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
        int index = getTestCaseLogRecordIndex(logRecord, reportEntity);
        return getJiraIssueCollection(index, logRecord, reportEntity);
    }

    default void updateJiraReport(TestCaseLogRecord logRecord, JiraIssueCollection jiraIssueCollection,
            ReportEntity reportEntity) throws JiraIntegrationException {
        int index = getTestCaseLogRecordIndex(logRecord, reportEntity);
        updateJiraReport(index, logRecord, jiraIssueCollection, reportEntity);
    }
}
