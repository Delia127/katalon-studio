package com.kms.katalon.execution.integration;

import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.console.entity.ConsoleOptionContributor;
import com.kms.katalon.execution.launcher.result.ExecutionEntityResult;

public interface ReportIntegrationContribution extends ConsoleOptionContributor {
    boolean isIntegrationActive(TestSuiteEntity testSuite);
    
    void uploadTestSuiteResult(TestSuiteEntity testSuite, String logFolder) throws Exception;
    
    void uploadTestSuiteCollection(String logFolder) throws Exception;
    
    default void notifyProccess(Object event, ExecutionEntityResult executedEntity) {
    	
    }
    
    default void printIntegrateMessage() {
        
    }
}
