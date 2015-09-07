package com.kms.katalon.composer.report.parts.integration;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public interface ReportTestCaseIntegrationViewBuilder {
	public AbstractReportTestCaseIntegrationView getIntegrationView(ReportEntity report,
			TestSuiteLogRecord testSuiteLogRecord);
}
