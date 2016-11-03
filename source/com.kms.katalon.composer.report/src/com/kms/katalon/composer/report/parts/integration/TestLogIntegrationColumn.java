package com.kms.katalon.composer.report.parts.integration;

import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public abstract class TestLogIntegrationColumn extends TestCaseIntegrationColumn implements TestCaseChangedEventListener {

    protected TestCaseLogRecord testCaseLogRecord;

    public TestLogIntegrationColumn(ReportEntity reportEntity) {
        super(reportEntity);
    }

    @Override
    public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
        this.testCaseLogRecord = testCaseLogRecord;
    }
    
    public TestCaseLogRecord getTestCaseLogRecord() {
        return testCaseLogRecord;
    }
}
