package com.kms.katalon.platform.internal.report;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.report.LogRecord;
import com.katalon.platform.api.report.LogStatus;
import com.katalon.platform.api.report.TestStepRecord;
import com.kms.katalon.core.logging.model.AbstractLogRecord;
import com.kms.katalon.core.logging.model.TestStatus;

public class TestStepRecordImpl implements TestStepRecord {
    private AbstractLogRecord source;

    private List<LogRecord> childRecords;

    public TestStepRecordImpl(AbstractLogRecord source) {
        this.source = source;
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
    public LogStatus getLogStatus() {
        return LogStatus.valueOf(source.getStatus().getStatusValue().name());
    }

    @Override
    public String getStackTrace() {
        TestStatus sourceStatus = source.getStatus();
        return sourceStatus != null ? sourceStatus.getStackTrace() : "";
    }

    @Override
    public List<LogRecord> getChildRecords() {
        if (source.getChildRecords() == null) {
            return Collections.emptyList();
        }
        if (childRecords == null) {
            childRecords = Arrays.asList(source.getChildRecords())
                    .stream()
                    .filter(child -> child instanceof TestStepRecordImpl)
                    .map(child -> (TestStepRecordImpl) child)
                    .map(record -> (LogRecord) record)
                    .collect(Collectors.toList());
        }
        return childRecords;
    }

    @Override
    public List<String> getAttachments() {
        return Arrays.asList(source.getAttachments());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TestStepRecordImpl)) {
            return false;
        }
        TestStepRecordImpl that = (TestStepRecordImpl) obj;
        return source.equals(that.source);
    }
}
