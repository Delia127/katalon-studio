package com.kms.katalon.platform.internal.report;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public class TestSuiteRecordImpl implements TestSuiteRecord {
    private final TestSuiteLogRecord source;

    private final ReportEntity report;

    private List<TestCaseRecord> childRecords;

    public TestSuiteRecordImpl(ReportEntity report, TestSuiteLogRecord source) {
        this.source = source;
        this.report = report;
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getDescription() {
        return source.getDescription();
    }

    @Override
    public String getMessage() {
        return source.getMessage();
    }

    @Override
    public long getStartTime() {
        return source.getStartTime();
    }

    @Override
    public long getEndTime() {
        return source.getEndTime();
    }

    @Override
    public String getReportId() {
        return report.getIdForDisplay();
    }

    @Override
    public String getTestSuiteId() {
        return source.getId();
    }

    @Override
    public List<TestCaseRecord> getTestCaseRecords() {
        if (source.getChildRecords() == null) {
            return Collections.emptyList();
        }
        if (childRecords == null) {
            childRecords = Arrays.asList(source.getChildRecords())
                    .stream()
                    .filter(child -> child instanceof TestCaseLogRecord)
                    .map(child -> (TestCaseLogRecord) child)
                    .map(testCaseRecord -> new TestCaseRecordImpl(testCaseRecord))
                    .collect(Collectors.toList());
        }
        return childRecords;
    }

    @Override
    public Map<String, String> getRunData() {
        return source.getRunData();
    }

    @Override
    public List<String> getLogFiles() {
        return source.getLogFiles();
    }

    @Override
    public List<String> getAttachments() {
        return Arrays.asList(source.getAttachments());
    }
}
