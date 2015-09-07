package com.kms.katalon.composer.integration.qtest.view.report;

import com.kms.katalon.composer.report.parts.integration.AbstractReportTestCaseIntegrationView;
import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.report.ReportEntity;

public class QTestIntegrationReportTestCaseViewBuilder implements ReportTestCaseIntegrationViewBuilder {

	@Override
	public AbstractReportTestCaseIntegrationView getIntegrationView(ReportEntity reportEntity,
			TestSuiteLogRecord testSuiteLogRecord) {
		return new QTestIntegrationReportTestCaseView(reportEntity, testSuiteLogRecord);
	}

}
