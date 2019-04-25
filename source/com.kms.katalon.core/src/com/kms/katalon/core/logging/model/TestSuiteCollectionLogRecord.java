package com.kms.katalon.core.logging.model;

import java.util.List;

public class TestSuiteCollectionLogRecord {

    private String testSuiteCollectionId;

    private String reportLocation;
    
    private List<TestSuiteLogRecord> testSuiteRecords;

    public String getTestSuiteCollectionId() {
        return testSuiteCollectionId;
    }

    public void setTestSuiteCollectionId(String testSuiteCollectionId) {
        this.testSuiteCollectionId = testSuiteCollectionId;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public List<TestSuiteLogRecord> getTestSuiteRecords() {
        return testSuiteRecords;
    }

    public void setTestSuiteRecords(List<TestSuiteLogRecord> testSuiteRecords) {
        this.testSuiteRecords = testSuiteRecords;
    }
}
