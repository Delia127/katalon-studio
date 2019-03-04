package com.kms.katalon.execution.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.katalon.platform.api.execution.TestSuiteCollectionExecutionContext;
import com.katalon.platform.api.execution.TestSuiteExecutionContext;

public class TestSuiteCollectionExecutionContextImpl implements TestSuiteCollectionExecutionContext {

    private final Builder builder;

    private TestSuiteCollectionExecutionContextImpl(Builder builder) {
        this.builder = builder;
    }

    @Override
    public String getId() {
        return builder.id;
    }

    @Override
    public String getSourceId() {
        return builder.sourceId;
    }

    @Override
    public long getStartTime() {
        return builder.startTime;
    }

    @Override
    public long getEndTime() {
        return builder.endTime;
    }

    @Override
    public String getReportLocation() {
        return new File(builder.projectLocation, builder.reportId).getAbsolutePath();
    }

    @Override
    public String getReportId() {
        return builder.reportId;
    }

    @Override
    public List<TestSuiteExecutionContext> getTestSuiteResults() {
        return builder.testSuiteContexts;
    }

    public static class Builder {
        private String id;

        private String sourceId;

        private long startTime;

        private long endTime;

        private String reportId;

        private String projectLocation;

        private List<TestSuiteExecutionContext> testSuiteContexts = new ArrayList<>();

        private Builder(String id, String sourceId) {
            this.id = id;
            this.sourceId = sourceId;
        }

        public static Builder create(String id, String sourceId) {
            return new Builder(id, sourceId);
        }

        public Builder withStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withEndTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder withReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder withProjectLocation(String projectLocation) {
            this.projectLocation = projectLocation;
            return this;
        }

        public Builder withTestSuiteContexts(List<TestSuiteExecutionContext> testSuiteContexts) {
            this.testSuiteContexts = testSuiteContexts;
            return this;
        }

        public TestSuiteCollectionExecutionContextImpl build() {
            return new TestSuiteCollectionExecutionContextImpl(this);
        }
    }
}
