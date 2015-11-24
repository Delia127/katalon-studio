package com.kms.katalon.execution.configuration.contributor;

import java.io.IOException;
import java.util.Map;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.exception.ExecutionException;

public interface IRunConfigurationContributor {
    public String getId();
    
    // Set preferred order for consistent with execution menu
    public int getPreferredOrder();

    public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput)
            throws IOException, ExecutionException;

    public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException, ExecutionException;
}
