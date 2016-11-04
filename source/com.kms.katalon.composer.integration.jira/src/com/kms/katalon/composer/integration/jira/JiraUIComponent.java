package com.kms.katalon.composer.integration.jira;

import java.io.IOException;

import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.jira.JiraCredential;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;
import com.kms.katalon.integration.jira.entity.JiraReport;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;

public interface JiraUIComponent {

    default JiraIntegrationSettingStore getSettingStore() {
        return new JiraIntegrationSettingStore(ProjectController.getInstance().getCurrentProject().getFolderLocation());
    }

    default String getHTMLIssueURLPrefix() throws IOException {
        return getSettingStore().getServerUrl() + StringConstants.HREF_BROWSE_ISSUE;
    }

    default JiraCredential getCredential() throws IOException {
        return getSettingStore().getJiraCredential();
    }

    default JiraIssueCollection getJiraIssueCollection(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
        return JiraObjectToEntityConverter
                .getOptionalJiraIssueCollection(reportEntity, getTestCaseLogRecordIndex(logRecord, reportEntity))
                .map(jiraIssue -> jiraIssue)
                .orElse(new JiraIssueCollection(logRecord.getId()));
    }

    default int getTestCaseLogRecordIndex(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
        return LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity).getChildIndex(logRecord);
    }

    default void updateJiraReport(TestCaseLogRecord logRecord, JiraIssueCollection jiraIssueCollection,
            ReportEntity reportEntity) throws JiraIntegrationException {
        JiraReport jiraReport = JiraObjectToEntityConverter.getJiraReport(reportEntity);
        jiraReport.getIssueCollectionMap().put(getTestCaseLogRecordIndex(logRecord, reportEntity),
                jiraIssueCollection);
        JiraObjectToEntityConverter.updateJiraReport(jiraReport, reportEntity);
    }
}
