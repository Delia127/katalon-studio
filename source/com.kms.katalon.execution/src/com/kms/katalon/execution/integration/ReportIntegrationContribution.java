package com.kms.katalon.execution.integration;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;

public interface ReportIntegrationContribution extends ConsoleOptionContributor {
    boolean isIntegrationActive(TestSuiteEntity testSuite);
    void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLog) throws Exception;
}
