package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.CustomRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;

public class CustomRunConfigurationContributor implements IRunConfigurationContributor {
    private String name;

    public CustomRunConfigurationContributor(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput)
            throws IOException, ExecutionException {
        return new CustomRunConfiguration(testCase, name);
    }

    @Override
    public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException, ExecutionException {
        return new CustomRunConfiguration(testSuite, name);
    }
    

}
