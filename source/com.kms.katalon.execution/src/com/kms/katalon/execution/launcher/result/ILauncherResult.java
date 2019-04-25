package com.kms.katalon.execution.launcher.result;

import com.kms.katalon.core.logging.model.TestStatus;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public interface ILauncherResult {
    int getTotalTestCases();
    
    int getNumPasses();
    
    int getNumFailures();
    
    int getNumErrors();
    
    int getNumIncomplete();
    
    int getNumSkips();

    public int getExecutedTestCases();
    
    public int getReturnCode();
    
    public TestStatusValue[] getResultValues();
    
    public TestStatus[] getStatuses();
}
