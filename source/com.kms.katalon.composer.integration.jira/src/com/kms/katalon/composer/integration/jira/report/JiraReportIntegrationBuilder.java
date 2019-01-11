package com.kms.katalon.composer.integration.jira.report;

import java.io.IOException;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.jira.JiraUIComponent;
import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.parts.integration.TestCaseIntegrationColumn;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.composer.report.parts.integration.TestLogIntegrationColumn;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;

public class JiraReportIntegrationBuilder implements ReportTestCaseIntegrationViewBuilder, JiraUIComponent {

    @Override
    public TestCaseLogDetailsIntegrationView getIntegrationDetails(ReportEntity report,
            TestSuiteLogRecord testSuiteLogRecord) {
        return new JiraReportTestLogView(report, testSuiteLogRecord);
    }

    @Override
    public TestCaseIntegrationColumn getTestCaseIntegrationColumn(ReportEntity report, TestSuiteLogRecord suiteRecord) {
        return new JiraReportTestCaseColumn(report, suiteRecord);
    }

    @Override
    public int getPreferredOrder() {
        return 1;
    }

    @Override
    public boolean isIntegrationEnabled(ProjectEntity project) {
        try {
            return !isJiraPluginEnabled() && getSettingStore().isIntegrationEnabled();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Override
    public TestLogIntegrationColumn getTestLogIntegrationColumn(ReportEntity report,
            TestSuiteLogRecord testSuiteRecord) {
        return new JiraReportTestLogColumn(report, testSuiteRecord);
    }

    @Override
    public String getName() {
        return "JIRA";
    }

}
