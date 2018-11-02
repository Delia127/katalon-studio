package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestCaseIntegrationColumn implements IntegrationColumnContributor {

    protected ReportEntity reportEntity;

    public TestCaseIntegrationColumn(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }
    
    protected static void openBrowserToLink(String url) {
        try {
            Program.launch(url);
        } catch (IllegalArgumentException exception) {
            LoggerSingleton.logError(exception);
        }
    }
}
