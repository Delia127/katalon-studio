package com.kms.katalon.core.context.internal;

import java.util.Map;

import com.kms.katalon.core.context.TestCaseContext;

public class InternalTestCaseContext implements TestCaseContext {
    private String testCaseStatus;

    private String testCaseId;

    private Map<String, Object> testCaseVariables;

    @Override
    public String getTestCaseStatus() {
        return testCaseStatus;
    }

    public void setTestCaseStatus(String testCaseStatus) {
        this.testCaseStatus = testCaseStatus;
    }

    @Override
    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    @Override
    public Map<String, Object> getTestCaseVariables() {
        return testCaseVariables;
    }

    public void setTestCaseVariables(Map<String, Object> testCaseVariables) {
        this.testCaseVariables = testCaseVariables;
    }
}
