package com.kms.katalon.execution.integration;

import java.util.Date;

import com.kms.katalon.core.logging.model.TestSuiteCollectionLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.launcher.result.ExecutionEntityResult;

public interface ReportIntegrationContribution extends ConsoleOptionContributor {
    boolean isIntegrationActive(TestSuiteEntity testSuite);
    
    void uploadTestSuiteResult(TestSuiteEntity testSuite, TestSuiteLogRecord suiteLogRecord) throws Exception;
    
    void uploadTestSuiteCollectionResult(TestSuiteCollectionEntity testSuiteCollection, TestSuiteCollectionLogRecord collectionLogRecord) throws Exception;
    
    default void notifyProccess(Object event, ExecutionEntityResult executedEntity) {
    	
    }
    
    default void sendTrackingActivity(Long organizationId, String machineId, String sessionId, Date startTime, Date endTime, String ksVersion) {
    }
    
    default void printIntegrateMessage() {
        
    }
}
