package com.kms.katalon.execution.entity;

public interface Rerunnable {
    
    int getPreviousRerunTimes();
    
    int getRemainingRerunTimes();
    
    boolean isRerunFailedTestCasesOnly();
}
