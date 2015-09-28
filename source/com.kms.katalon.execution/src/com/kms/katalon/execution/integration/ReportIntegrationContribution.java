package com.kms.katalon.execution.integration;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public interface ReportIntegrationContribution {
	public void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLog) throws Exception;
}
