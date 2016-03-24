package com.kms.katalon.execution.integration;

import java.util.List;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.entity.ConsoleOption;

public interface ReportIntegrationContribution {
    public void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLog) throws Exception;
    public List<ConsoleOption<?>> getIntegrationCommands();
}
