package com.kms.katalon.composer.report.parts.integration;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;

public interface ReportTestCaseIntegrationViewBuilder {
    String getName();

    TestCaseLogDetailsIntegrationView getIntegrationDetails(ReportEntity report,
            TestSuiteLogRecord testSuiteLogRecord);

    TestCaseIntegrationColumn getTestCaseIntegrationColumn(ReportEntity report, TestSuiteLogRecord suiteRecord);
    
    TestLogIntegrationColumn getTestLogIntegrationColumn(ReportEntity reportt, TestSuiteLogRecord suiteRecord);

    int getPreferredOrder();

    boolean isIntegrationEnabled(ProjectEntity project);
}
