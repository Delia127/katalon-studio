package com.kms.katalon.dal.exception;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteTestCaseLinkPair;

public class TestDataReferredException extends DALException {

    private static final long serialVersionUID = -3558443055580890276L;

    private List<TestSuiteTestCaseLinkPair> testSuiteTestCaseLinkPairs;

    public TestDataReferredException(String message) {
        super(message);
    }

    public void addReference(TestSuiteEntity testSuiteEntity, TestSuiteTestCaseLink testCaseLink) {
        getTestSuiteTestCaseLinkPairs().add(new TestSuiteTestCaseLinkPair(testSuiteEntity, testCaseLink));
    }

    public List<TestSuiteTestCaseLinkPair> getTestSuiteTestCaseLinkPairs() {
        if (testSuiteTestCaseLinkPairs == null) {
            testSuiteTestCaseLinkPairs = new ArrayList<TestSuiteTestCaseLinkPair>();
        }
        return testSuiteTestCaseLinkPairs;
    }

    public void setTestSuiteTestCaseLinkPairs(List<TestSuiteTestCaseLinkPair> testSuiteTestCaseLinkPairs) {
        this.testSuiteTestCaseLinkPairs = testSuiteTestCaseLinkPairs;
    }
}
