package com.kms.katalon.integration.jira;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;
import com.kms.katalon.integration.jira.entity.JiraReport;
import com.kms.katalon.integration.jira.setting.JiraIntegrationSettingStore;

public interface JiraComponent {

    default boolean isJiraPluginEnabled() {
        return Platform.getBundle("com.katalon.katalon-studio-jira-plugin") != null;
    }

    default ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    default JiraIntegrationSettingStore getSettingStore() {
        return new JiraIntegrationSettingStore(getCurrentProject().getFolderLocation());
    }

    default JiraCredential getCredential() throws IOException, JiraIntegrationException {
        try {
            return getSettingStore().getJiraCredential();
        } catch (GeneralSecurityException e) {
            throw new JiraIntegrationException(e);
        }
    }

    default void updateJiraReport(int index, TestCaseLogRecord logRecord, JiraIssueCollection jiraIssueCollection,
            ReportEntity reportEntity) throws JiraIntegrationException {
        JiraReport jiraReport = JiraObjectToEntityConverter.getJiraReport(reportEntity);
        jiraReport.getIssueCollectionMap().put(index, jiraIssueCollection);
        JiraObjectToEntityConverter.updateJiraReport(jiraReport, reportEntity);
    }

    default JiraIssueCollection getJiraIssueCollection(int index, TestCaseLogRecord logRecord,
            ReportEntity reportEntity) {
        return JiraObjectToEntityConverter.getOptionalJiraIssueCollection(reportEntity, index)
                .map(jiraIssue -> jiraIssue)
                .orElse(new JiraIssueCollection(logRecord.getId()));
    }
}
