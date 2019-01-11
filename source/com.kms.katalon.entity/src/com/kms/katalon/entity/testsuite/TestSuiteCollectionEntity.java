package com.kms.katalon.entity.testsuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.Entity;
import com.kms.katalon.entity.file.FileEntity;

public class TestSuiteCollectionEntity extends FileEntity {
    public enum ExecutionMode {
        SEQUENTIAL, PARALLEL
    }
    private static final long serialVersionUID = -611439373065144113L;

    public static final String FILE_EXTENSION = ".ts";

    public static final int DF_CONCURRENT_INSTANCES = 8;
    
    public static final int MIN_CONCURRENT_INSTANCES = 1;

    public static final int MAX_CONCURRENT_INSTANCES = 1000;

    private List<TestSuiteRunConfiguration> testSuiteRunConfigurations;
    
    private ExecutionMode executionMode;
    
    private int maxConcurrentInstances = DF_CONCURRENT_INSTANCES;

    public List<TestSuiteRunConfiguration> getTestSuiteRunConfigurations() {
        if (testSuiteRunConfigurations == null) {
            testSuiteRunConfigurations = new ArrayList<>();
        }
        return testSuiteRunConfigurations;
    }

    public void setTestSuiteRunConfigurations(List<TestSuiteRunConfiguration> testSuiteRunConfigurations) {
        this.testSuiteRunConfigurations = testSuiteRunConfigurations;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        result = prime * result + ((testSuiteRunConfigurations == null) ? 0 : testSuiteRunConfigurations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }

        TestSuiteCollectionEntity other = (TestSuiteCollectionEntity) obj;
        EqualsBuilder equalBuilder = new EqualsBuilder();
        return equalBuilder.append(getTag(), other.getTag())
                .append(getTestSuiteRunConfigurations(), other.getTestSuiteRunConfigurations())
                .isEquals();
    }

    public void reuseWrappers(TestSuiteCollectionEntity src) {
        List<TestSuiteRunConfiguration> runConfigs = new ArrayList<>();
        for (TestSuiteRunConfiguration eachSourceConfig : src.getTestSuiteRunConfigurations()) {
            runConfigs.add(TestSuiteRunConfiguration.cloneFrom(eachSourceConfig));
        }
        this.setTestSuiteRunConfigurations(runConfigs);
        this.setExecutionMode(src.getExecutionMode());
        this.setMaxConcurrentInstances(src.getMaxConcurrentInstances());
    }

    @Override
    public Entity clone() {
        return super.clone();
    }

    public boolean isAnyRunEnabled() {
        for (TestSuiteRunConfiguration testSuiteRunConfig : getTestSuiteRunConfigurations()) {
            if (testSuiteRunConfig.isRunEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllRunEnabled() {
        for (TestSuiteRunConfiguration testSuiteRunConfig : getTestSuiteRunConfigurations()) {
            if (!testSuiteRunConfig.isRunEnabled()) {
                return false;
            }
        }
        return true;
    }

    public void enableRunForAll(boolean runEnabled) {
        for (TestSuiteRunConfiguration testSuiteRunConfig : getTestSuiteRunConfigurations()) {
            testSuiteRunConfig.setRunEnabled(runEnabled);
        }
    }

    public List<TestSuiteRunConfiguration> findRunConfigurations(TestSuiteEntity testSuite) {
        if (testSuite == null) {
            return Collections.emptyList();
        }
        List<TestSuiteRunConfiguration> runConfigurations = new ArrayList<>();
        for (TestSuiteRunConfiguration config : getTestSuiteRunConfigurations()) {
            if (testSuite.equals(config.getTestSuiteEntity())) {
                runConfigurations.add(config);
            }
        }
        return runConfigurations;
    }

    public boolean hasTestSuiteReferences(TestSuiteEntity testSuite) {
        return !findRunConfigurations(testSuite).isEmpty();
    }

    public ExecutionMode getExecutionMode() {
        if (executionMode == null) {
            return ExecutionMode.SEQUENTIAL;
        }
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public boolean isEmpty() {
        return testSuiteRunConfigurations.isEmpty();
    }

    public int getMaxConcurrentInstances() {
        return maxConcurrentInstances;
    }

    public void setMaxConcurrentInstances(int maxConcurrentInstances) {
        this.maxConcurrentInstances = maxConcurrentInstances;
    }
}
