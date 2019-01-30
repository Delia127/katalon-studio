package com.kms.katalon.integration.jira.report;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.integration.jira.JiraComponent;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.constant.JiraIntegrationMessageConstants;
import com.kms.katalon.integration.jira.entity.JiraIssue;
import com.kms.katalon.logging.LogUtil;

public class JiraReportIntegration implements ReportIntegrationContribution, JiraComponent {

    private JiraReportService reportService = new JiraReportService();

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
            return !isJiraPluginEnabled() && getSettingStore().isIntegrationEnabled()
                    && getSettingStore().isSubmitTestResultAutomatically();
        } catch (IOException e) {
            LogUtil.logError(e);
            return false;
        }
    }

    @Override
    public void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLog) throws Exception {
        LogUtil.printOutputLine(JiraIntegrationMessageConstants.MSG_SEND_TEST_RESULT);

        ILogRecord[] childRecords = suiteLog.getChildRecords();
        File reportZipFile = null;
        for (int index = 0; index < childRecords.length; index++) {
            ILogRecord child = childRecords[index];
            if (!(child instanceof TestCaseLogRecord)) {
                continue;
            }
            TestCaseLogRecord logRecord = (TestCaseLogRecord) child;
            try {
                JiraIssue issue = JiraObjectToEntityConverter
                        .getJiraIssue(TestCaseController.getInstance().getTestCaseByDisplayId(logRecord.getId()));
                if (issue == null) {
                    continue;
                }
                if (reportZipFile == null || !reportZipFile.exists()) {
                    reportZipFile = reportService.zipReportFolder(new File(suiteLog.getLogFolder()));
                }
                reportService.uploadTestCaseReport(logRecord, issue, reportZipFile);
                LogUtil.printOutputLine(MessageFormat.format(JiraIntegrationMessageConstants.MSG_SEND_TEST_RESULT_SENT,
                        issue.getKey()));
            } catch (Exception e) {
                LogUtil.printAndLogError(e);
            }
        }
        if (reportZipFile != null && reportZipFile.exists()) {
            FileUtils.deleteQuietly(reportZipFile);
        }
    }
}
