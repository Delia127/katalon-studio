package com.kms.katalon.entity.testsuite;

import com.kms.katalon.entity.file.ClonableObject;

public class TestSuiteRunConfiguration extends ClonableObject {
    private static final long serialVersionUID = 5285329867130450757L;

    private TestSuiteEntity testSuiteEntity;

    private boolean runEnabled;

    private RunConfigurationDescription configuration;

    public TestSuiteEntity getTestSuiteEntity() {
        return testSuiteEntity;
    }

    public void setTestSuiteEntity(TestSuiteEntity testSuiteEntity) {
        this.testSuiteEntity = testSuiteEntity;
    }

    public boolean isRunEnabled() {
        return runEnabled;
    }

    public void setRunEnabled(boolean runEnabled) {
        this.runEnabled = runEnabled;
    }

    public RunConfigurationDescription getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RunConfigurationDescription configuration) {
        this.configuration = configuration;
    }

    public static TestSuiteRunConfiguration newInstance(TestSuiteEntity testSuiteEntity,
            RunConfigurationDescription config) {
        TestSuiteRunConfiguration newInstance = new TestSuiteRunConfiguration();
        newInstance.setTestSuiteEntity(testSuiteEntity);
        newInstance.setRunEnabled(true);
        newInstance.setConfiguration(config);
        return newInstance;
    }

    public static TestSuiteRunConfiguration cloneFrom(TestSuiteRunConfiguration that) {
        TestSuiteRunConfiguration clone = (TestSuiteRunConfiguration) that.clone();
        clone.setTestSuiteEntity(that.getTestSuiteEntity());
        return clone;
    }

}
