package com.kms.katalon.composer.testsuite.collection.transfer;

import java.io.Serializable;

import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteRunConfigurationTransferData implements Serializable {

    private static final long serialVersionUID = 1L;

    private TestSuiteRunConfiguration testSuiteRunConfiguration;

    private String testSuiteCollectionID;

    public TestSuiteRunConfigurationTransferData(TestSuiteRunConfiguration testSuiteRunConfiguration, String id) {
        this.testSuiteRunConfiguration = testSuiteRunConfiguration;
        this.testSuiteCollectionID = id;
    }

    public TestSuiteRunConfiguration getTestSuiteRunConfiguration() {
        return testSuiteRunConfiguration;
    }

    public void setTestSuiteRunConfiguration(TestSuiteRunConfiguration testSuiteRunConfiguration) {
        this.testSuiteRunConfiguration = testSuiteRunConfiguration;
    }

    public String getTestSuiteCollectionID() {
        return testSuiteCollectionID;
    }

    public void setTestSuiteCollectionID(String testSuiteCollectionID) {
        this.testSuiteCollectionID = testSuiteCollectionID;
    }

}
