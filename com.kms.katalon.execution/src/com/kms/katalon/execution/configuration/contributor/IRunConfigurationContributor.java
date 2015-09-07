package com.kms.katalon.execution.configuration.contributor;

import java.util.Map;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.IRunConfiguration;

public interface IRunConfigurationContributor {
	public String getId();

	public IRunConfiguration getRunConfiguration(TestCaseEntity testCase, Map<String, String> runInput);

	public IRunConfiguration getRunConfiguration(TestSuiteEntity testSuite, Map<String, String> runInput);
}
