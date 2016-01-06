package com.kms.katalon.composer.execution.listener;

import java.io.File;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.handlers.ExecuteHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.configuration.AbstractRunConfiguration;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.launcher.model.LaunchMode;

public class TestSuiteExecutionJobCompletedListener implements EventHandler {
    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.JOB_COMPLETED)) {
            try {
                Object[] datas = (Object[]) event.getProperty(IEventBroker.DATA);
                TestSuiteEntity testSuite = (TestSuiteEntity) datas[0];
                LaunchMode launchMode = (LaunchMode) datas[1];
                IRunConfiguration runConfig = (IRunConfiguration) datas[2];
                int reRunTime = (int) datas[3];
                File logFile = (File) datas[4];
                List<String> passedTestCaseIds = (List<String>) datas[5];

                if (logFile != null && logFile.exists()) {
                    TestSuiteLogRecord testSuiteRecord = ReportUtil.generate(logFile.getParent());
                    if (testSuiteRecord != null
                            && testSuiteRecord.getStatus().getStatusValue() != TestStatusValue.PASSED
                            && reRunTime < testSuite.getNumberOfRerun()) {
                        if (runConfig instanceof AbstractRunConfiguration) {
                            AbstractRunConfiguration abstractRunConfiguration = (AbstractRunConfiguration) runConfig;
                            abstractRunConfiguration.generateLogFolder(testSuite);
                            abstractRunConfiguration.generateLogFilePath(testSuite);
                            if (testSuite.isRerunFailedTestCasesOnly()) {
                                collectPassedTestCaseIds(testSuiteRecord, passedTestCaseIds);
                            }
                            ExecuteHandler.executeTestSuite(testSuite, launchMode, runConfig, reRunTime + 1,
                                    passedTestCaseIds);
                        }
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private static void collectPassedTestCaseIds(TestSuiteLogRecord testSuiteRecord, List<String> passedTestCaseIds) {
        if (passedTestCaseIds == null) {
            return;
        }
        for (ILogRecord childLogRecord : testSuiteRecord.getChildRecords()) {
            if (childLogRecord instanceof TestCaseLogRecord
                    && childLogRecord.getStatus().getStatusValue() == TestStatusValue.PASSED) {
                passedTestCaseIds.add(childLogRecord.getName());
            }
        }
    }
}
