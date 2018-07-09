package com.kms.katalon.composer.testcase.model;

import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ExecuteFromTestStepEntity {
    private String rawScript;

    private TestCaseEntity testCase;

    private String sessionId;

    private String remoteServerUrl;

    private String driverTypeName;

    public String getRawScript() {
        return rawScript;
    }

    public void setRawScript(String rawScript) {
        this.rawScript = rawScript.replace("\\", "\\\\");
    }

    public TestCaseEntity getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseEntity testCase) {
        this.testCase = testCase;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRemoteServerUrl() {
        return remoteServerUrl;
    }

    public void setRemoteServerUrl(String remoteServerUrl) {
        this.remoteServerUrl = remoteServerUrl;
    }

    public String getDriverTypeName() {
        return driverTypeName;
    }

    public void setDriverTypeName(String driverTypeName) {
        this.driverTypeName = driverTypeName;
    }

}
