package com.kms.katalon.execution.launcher;

import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public interface ILauncherResult {
    int getTotalTestCases();
    
    int getNumPasses();
    
    int getNumFailures();
    
    int getNumErrors();
    
    int getNumIncomplete();
    
    public int getExecutedTestCases();
    
    public int getReturnCode();
    
    public TestStatusValue[] getResultValues();
}
