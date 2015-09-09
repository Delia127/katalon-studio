package com.kms.katalon.entity.testsuite;

import com.kms.katalon.entity.link.TestSuiteTestCaseLink;

public class TestSuiteTestCaseLinkPair {
    
    private TestSuiteEntity testSuiteEntity;
    private TestSuiteTestCaseLink testCaseLink;
    
    public TestSuiteTestCaseLinkPair(TestSuiteEntity left, TestSuiteTestCaseLink right) {
        setTestSuiteEntity(left);
        setTestCaseLink(right);
    }

    public TestSuiteEntity getTestSuiteEntity() {
        return testSuiteEntity;
    }

    public void setTestSuiteEntity(TestSuiteEntity testSuiteEntity) {
        this.testSuiteEntity = testSuiteEntity;
    }

    public TestSuiteTestCaseLink getTestCaseLink() {
        return testCaseLink;
    }

    public void setTestCaseLink(TestSuiteTestCaseLink testCaseLink) {
        this.testCaseLink = testCaseLink;
    }
}
