package com.kms.katalon.integration.analytics.report;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.integration.analytics.AnalyticsComponent;
import com.kms.katalon.logging.LogUtil;

public class AnalyticsReportIntegration implements ReportIntegrationContribution, AnalyticsComponent {
    
    private AnalyticsReportService reportService = new AnalyticsReportService();

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        return Collections.emptyList();
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
    }

    @Override
    public boolean isIntegrationActive(TestSuiteEntity testSuite) {
        try {
            return getSettingStore().isIntegrationEnabled()
                    && getSettingStore().isAutoSubmit();
        } catch (IOException e) {
            LogUtil.logError(e);
            return false;
        }
    }

    @Override
    public void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLog) throws Exception {
        reportService.upload(suiteLog.getLogFolder());
    }

}
