package com.kms.katalon.composer.report.parts.integration;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerColumn;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestLogIntegrationColumn implements TestCaseChangedEventListener, IntegrationColumnContributor {

    protected ReportEntity reportEntity;

    private TestCaseLogRecord testCaseLogRecord;

    private final TestSuiteLogRecord testSuiteLogRecord;

    public TestLogIntegrationColumn(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord) {
        this.reportEntity = reportEntity;
        this.testSuiteLogRecord = testSuiteLogRecord;
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }

    public abstract ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex);

    @Override
    public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
        this.testCaseLogRecord = testCaseLogRecord;
    }

    public TestCaseLogRecord getTestCaseLogRecord() {
        return testCaseLogRecord;
    }

    public TestSuiteLogRecord getTestSuiteLogRecord() {
        return testSuiteLogRecord;
    }
}
