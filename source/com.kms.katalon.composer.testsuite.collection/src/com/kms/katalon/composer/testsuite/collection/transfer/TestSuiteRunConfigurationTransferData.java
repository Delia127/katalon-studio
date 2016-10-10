package com.kms.katalon.composer.testsuite.collection.transfer;

import java.io.Serializable;

import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteRunConfigurationTransferData implements Serializable {

    private static final long serialVersionUID = 1L;

    private TestSuiteRunConfiguration testSuiteRunConfiguration;

    private TestSuiteCollectionEntity testSuiteCollection;

    public TestSuiteRunConfigurationTransferData(TestSuiteCollectionEntity testSuiteCollection,
            TestSuiteRunConfiguration testSuiteRunConfiguration) {
        this.testSuiteRunConfiguration = testSuiteRunConfiguration;
        this.testSuiteCollection = testSuiteCollection;
    }

    public TestSuiteRunConfiguration getTestSuiteRunConfiguration() {
        return testSuiteRunConfiguration;
    }

    public void setTestSuiteRunConfiguration(TestSuiteRunConfiguration testSuiteRunConfiguration) {
        this.testSuiteRunConfiguration = testSuiteRunConfiguration;
    }

    public TestSuiteCollectionEntity getTestSuiteCollection() {
        return testSuiteCollection;
    }

    public void setTestSuiteCollection(TestSuiteCollectionEntity testSuiteCollection) {
        this.testSuiteCollection = testSuiteCollection;
    }

}
