package com.kms.katalon.execution.entity;

public interface Rerunable {
    
    int getPreviousRerunTimes();
    
    int getRemainingRerunTimes();
    
    boolean isRerunFailedTestCasesOnly();

    Rerunable mergeWith(Rerunable rerunable);
}
