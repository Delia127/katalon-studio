package com.kms.katalon.execution.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.contributor.IRunConfigurationContributor;
import com.kms.katalon.execution.entity.IRunConfiguration;

public class BuiltinRunConfigurationFactory {
	private static BuiltinRunConfigurationFactory _instance; 
	private List<IRunConfigurationContributor> runConfigurationContributors;

	private BuiltinRunConfigurationFactory() {
		runConfigurationContributors = new ArrayList<IRunConfigurationContributor>();
	}

	public static BuiltinRunConfigurationFactory getInstance() {
		if (_instance == null) {
			_instance = new BuiltinRunConfigurationFactory();
		}
		return _instance;
	}
	
	public void addRunConfigurationContributor(IRunConfigurationContributor runConfigurationContributor) {
		runConfigurationContributors.add(runConfigurationContributor);
	}
	
	public IRunConfiguration getRunConfiguration(String id, TestCaseEntity testCase, Map<String, String> runInput) {
		for (IRunConfigurationContributor runConfigurationContributor : runConfigurationContributors) {
			if (runConfigurationContributor.getId().equals(id)) {
				return runConfigurationContributor.getRunConfiguration(testCase, runInput);
			}
		}
		return null;
	}
	
	public IRunConfiguration getRunConfiguration(String id, TestSuiteEntity testSuite, Map<String, String> runInput) {
		for (IRunConfigurationContributor runConfigurationContributor : runConfigurationContributors) {
			if (runConfigurationContributor.getId().equals(id)) {
				return runConfigurationContributor.getRunConfiguration(testSuite, runInput);
			}
		}
		return null;
	}
	
	public IRunConfigurationContributor[] getAllRunConfigurationContributors() {
	    return runConfigurationContributors.toArray(new IRunConfigurationContributor[runConfigurationContributors.size()]);
	}
}
