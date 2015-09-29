package com.kms.katalon.execution.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;

public class RunConfigurationCollector {
    private static RunConfigurationCollector _instance;
    private List<IRunConfigurationContributor> runConfigurationContributors;
    private List<IRunConfigurationContributor> customRunConfigurationContributors;

    private RunConfigurationCollector() {
        runConfigurationContributors = new ArrayList<IRunConfigurationContributor>();
        customRunConfigurationContributors = new ArrayList<IRunConfigurationContributor>();
    }

    public static RunConfigurationCollector getInstance() {
        if (_instance == null) {
            _instance = new RunConfigurationCollector();
        }
        return _instance;
    }

    public void addBuiltinRunConfigurationContributor(IRunConfigurationContributor runConfigurationContributor) {
        runConfigurationContributors.add(runConfigurationContributor);
    }
    
    public void addCustomRunConfigurationContributor(IRunConfigurationContributor runConfigurationContributor) {
        customRunConfigurationContributors.add(runConfigurationContributor);
    }

    public IRunConfiguration getRunConfiguration(String id, TestCaseEntity testCase, Map<String, String> runInput) throws IOException {
        for (IRunConfigurationContributor runConfigurationContributor : runConfigurationContributors) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testCase, runInput);
            }
        }
        for (IRunConfigurationContributor runConfigurationContributor : customRunConfigurationContributors) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testCase, runInput);
            }
        }
        return null;
    }

    public IRunConfiguration getRunConfiguration(String id, TestSuiteEntity testSuite, Map<String, String> runInput)
            throws IOException {
        for (IRunConfigurationContributor runConfigurationContributor : runConfigurationContributors) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testSuite, runInput);
            }
        }
        for (IRunConfigurationContributor runConfigurationContributor : customRunConfigurationContributors) {
            if (runConfigurationContributor.getId().equals(id)) {
                return runConfigurationContributor.getRunConfiguration(testSuite, runInput);
            }
        }
        return null;
    }

    public IRunConfigurationContributor[] getAllBuiltinRunConfigurationContributors() {
        return runConfigurationContributors.toArray(new IRunConfigurationContributor[runConfigurationContributors
                .size()]);
    }
    
    public IRunConfigurationContributor[] getAllCustomRunConfigurationContributors() {
        return customRunConfigurationContributors.toArray(new IRunConfigurationContributor[customRunConfigurationContributors
                .size()]);
    }
}
