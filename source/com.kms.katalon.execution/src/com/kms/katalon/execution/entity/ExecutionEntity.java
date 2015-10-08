package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;

public class ExecutionEntity {
	private TestSuiteEntity testSuite;
	private List<IRunConfiguration> runConfigurations;
	private String reportFolderPath;

	public List<IRunConfiguration> getRunConfigurations() {
		if (runConfigurations == null) {
			runConfigurations = new ArrayList<IRunConfiguration>();
		}
		return runConfigurations;
	}

	public void setRunConfigurations(List<IRunConfiguration> runConfigurations) {
		this.runConfigurations = runConfigurations;
	}

	public TestSuiteEntity getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(TestSuiteEntity testSuite) {
		this.testSuite = testSuite;
	}

	public String getReportFolderPath() {
		return reportFolderPath;
	}

	public void setReportFolderPath(String reportFolderPath) {
		this.reportFolderPath = reportFolderPath;
	}
}
