package com.kms.katalon.composer.integration.qtest.model;

import java.util.List;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.entity.QTestSuite;

public class TestSuiteQTestSuitePair {
    private TestSuiteEntity testSuite;
    public TestSuiteEntity getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuiteEntity testSuite) {
        this.testSuite = testSuite;
    }

    public List<QTestSuite> getQTestSuites() {
        return qTestSuites;
    }

    public void setQTestSuites(List<QTestSuite> qTestSuites) {
        this.qTestSuites = qTestSuites;
    }

    private List<QTestSuite> qTestSuites;
    
    public TestSuiteQTestSuitePair(TestSuiteEntity left, List<QTestSuite> right) {
        testSuite = left;
        qTestSuites = right;
    }
}
