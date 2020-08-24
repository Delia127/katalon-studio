package com.kms.katalon.integration.analytics.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.logging.model.TestSuiteCollectionLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOption;
import com.kms.katalon.execution.console.entity.LongConsoleOption;
import com.kms.katalon.execution.entity.IExecutedEntity;
import com.kms.katalon.execution.entity.ReportFolder;
import com.kms.katalon.execution.entity.TestSuiteCollectionExecutedEntity;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.integration.ReportIntegrationContribution;
import com.kms.katalon.execution.launcher.result.ExecutionEntityResult;
import com.kms.katalon.integration.analytics.AnalyticsComponent;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestRun;
import com.kms.katalon.integration.analytics.entity.AnalyticsTracking;
import com.kms.katalon.logging.LogUtil;

public class AnalyticsReportIntegration implements ReportIntegrationContribution, AnalyticsComponent {
    
    public static final String TESTOPS_RELEASE_ID_CONSOLE_OPTION_NAME = "testOpsReleaseId";
    
    private static Long TESTOPS_RELEASE_ID = null;
    
    public static final LongConsoleOption TESTOPS_RELEASE_ID_CONSOLE_OPTION = new LongConsoleOption() {
        
        @Override
        public String getOption() {
            return TESTOPS_RELEASE_ID_CONSOLE_OPTION_NAME;
        }
        
        @Override 
        public Long getValue() {
            return TESTOPS_RELEASE_ID;
        }
    };
    
    private AnalyticsReportService reportService = new AnalyticsReportService();

    @Override
    public List<ConsoleOption<?>> getConsoleOptionList() {
        List<ConsoleOption<?>> integrationCommandList = new ArrayList<ConsoleOption<?>>();
        integrationCommandList.add(TESTOPS_RELEASE_ID_CONSOLE_OPTION);
        return integrationCommandList;
    }

    @Override
    public void setArgumentValue(ConsoleOption<?> consoleOption, String argumentValue) throws Exception {
        if (StringUtils.isBlank(argumentValue)) {
            return;
        }
        
        if (consoleOption == TESTOPS_RELEASE_ID_CONSOLE_OPTION) {
            try {
                TESTOPS_RELEASE_ID = Long.parseLong(argumentValue.trim());
            } catch (Exception e) {
                LogUtil.logError(e);
            }
            
        }
    }

    @Override
    public boolean isIntegrationActive(TestSuiteEntity testSuite) {
        try {
            return getSettingStore().isIntegrationEnabled();
        } catch (Exception e) {
            LogUtil.logError(e);
            return false;
        }
    }

    @Override
    public void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLogRecord) throws Exception {
        String collectionId = suiteLogRecord.getTestSuiteCollectionId();
        if (StringUtils.isNotBlank(collectionId)) {
            return;
        } else {
            ReportFolder reportFolder = new ReportFolder(suiteLogRecord.getLogFolder());
            reportService.upload(reportFolder);
        }
    }
    
    @Override
    public void uploadTestSuiteCollectionResult(TestSuiteCollectionEntity testSuiteCollection,
            TestSuiteCollectionLogRecord collectionLogRecord) throws Exception {
        List<TestSuiteLogRecord> suiteLogRecords = collectionLogRecord.getTestSuiteRecords();
        List<String> paths = new ArrayList<>();
        for (TestSuiteLogRecord suiteLogRecord : suiteLogRecords) {
            paths.add(suiteLogRecord.getLogFolder());
        }
        paths.add(collectionLogRecord.getReportLocation() + "/" + collectionLogRecord.getTestSuiteCollectionId());
        ReportFolder reportFolder = new ReportFolder(paths);
        reportService.upload(reportFolder);
    }
    
    @Override
    public void printIntegrateMessage() {
        LogUtil.printOutputLine(IntegrationAnalyticsMessages.MSG_INTEGRATE_WITH_KA);
    }
    
    @Override
    public void notifyProccess(Object event, ExecutionEntityResult result) {
    	try {
			boolean integrationActive = getSettingStore().isIntegrationEnabled();
			if (integrationActive) {
				IExecutedEntity executedEntity = result.getExecutedEntity();
				if (executedEntity instanceof TestSuiteExecutedEntity) {
					AnalyticsTestRun testRun = new AnalyticsTestRun();
					testRun.setName(result.getName());
					testRun.setSessionId(result.getSessionId());
					if (result.getTestStatusValue() != null) {
						testRun.setStatus(result.getTestStatusValue().name());
					}
					testRun.setTestSuiteId(executedEntity.getSourceId());
					testRun.setEnd(result.isEnd());
					reportService.updateExecutionProccess(testRun);
				} else if (executedEntity instanceof TestSuiteCollectionExecutedEntity) {
					AnalyticsTestRun testRun = new AnalyticsTestRun();
					testRun.setSessionId(result.getSessionId());
					testRun.setEnd(result.isEnd());
					reportService.updateExecutionProccess(testRun);
				}
			}
		} catch (Exception  e) {
			LogUtil.logError(e);
		}
    }
    
    @Override
    public void sendTrackingActivity(Long organizationId, String machineId, String sessionId, Date startTime, Date endTime, String ksVersion) {
        try {
            AnalyticsTracking trackingInfo = new AnalyticsTracking();
            trackingInfo.setMachineId(machineId);
            trackingInfo.setSessionId(sessionId);
            trackingInfo.setStartTime(startTime);
            trackingInfo.setEndTime(endTime);
            trackingInfo.setKsVersion(ksVersion);
            trackingInfo.setOrganizationId(organizationId);
            reportService.sendTrackingActivity(trackingInfo);
        } catch (Exception e) {
//            LogUtil.logError(e);
        }
    }

   
}
