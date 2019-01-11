package com.kms.katalon.composer.report.platform;

import java.util.List;

import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;

public interface PlatformReportIntegrationViewBuilder {
    List<ReportTestCaseIntegrationViewBuilder> getIntegrationViews();
}
