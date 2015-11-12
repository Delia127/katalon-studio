package com.kms.katalon.composer.integration.qtest.model;

import java.util.List;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public class ReportTestCaseLogPair {
    private ReportEntity reportEntity;
    private List<TestCaseLogRecord> testCaseLogs;

    public ReportTestCaseLogPair(ReportEntity left, List<TestCaseLogRecord> right) {
        reportEntity = left;
        setTestCaseLogs(right);
    }

    public ReportEntity getReportEntity() {
        return reportEntity;
    }

    public void setReportEntity(ReportEntity reportEntity) {
        this.reportEntity = reportEntity;
    }


    public List<TestCaseLogRecord> getTestCaseLogs() {
        return testCaseLogs;
    }

    public void setTestCaseLogs(List<TestCaseLogRecord> testCaseLogs) {
        this.testCaseLogs = testCaseLogs;
    }
}
