package com.kms.katalon.composer.report.parts.integration;

import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestCaseIntegrationColumn implements IntegrationColumnContributor {

    protected ReportEntity reportEntity;

    public TestCaseIntegrationColumn(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }
}
