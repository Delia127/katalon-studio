package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestCaseIntegrationColumn implements IntegrationColumnContributor {

    protected ReportEntity reportEntity;

    private final TestSuiteLogRecord testSuiteLogRecord;

    public TestCaseIntegrationColumn(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        this.reportEntity = reportEntity;
        this.testSuiteLogRecord = testSuiteLogRecord;
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }

    public abstract ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex);

    protected static void openBrowserToLink(String url) {
        try {
            Program.launch(url);
        } catch (IllegalArgumentException exception) {
            LoggerSingleton.logError(exception);
        }
    }

    public TestSuiteLogRecord getTestSuiteLogRecord() {
        return testSuiteLogRecord;
    }
}
