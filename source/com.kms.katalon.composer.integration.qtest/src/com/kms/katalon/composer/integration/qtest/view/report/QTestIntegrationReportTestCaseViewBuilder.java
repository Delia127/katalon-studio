package com.kms.katalon.composer.integration.qtest.view.report;

import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogColumnIntegrationView;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationReportTestCaseViewBuilder implements ReportTestCaseIntegrationViewBuilder {

    @Override
    public TestCaseLogDetailsIntegrationView getIntegrationDetails(ReportEntity reportEntity,
            TestSuiteLogRecord testSuiteLogRecord) {
        return new QTestIntegrationReportTestCaseView(reportEntity, testSuiteLogRecord);
    }

    @Override
    public TestCaseLogColumnIntegrationView getIntegrationColumn(ReportEntity reportEntity) {
        return new QTestIntegrationReportTestCaseColumnView(reportEntity);
    }

    @Override
    public int getPreferredOrder() {
        return 0;
    }

    @Override
    public boolean isIntegrationEnabled(ProjectEntity project) {
        return QTestSettingStore.isIntegrationActive(project.getFolderLocation());
    }
}
