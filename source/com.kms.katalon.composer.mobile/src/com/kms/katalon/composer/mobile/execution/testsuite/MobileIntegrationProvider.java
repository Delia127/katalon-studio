package com.kms.katalon.composer.mobile.execution.testsuite;

public interface MobileIntegrationProvider {
  
    int getPreferedOrder();
    
    MobileTestExecutionDriverEntry getExecutionEntry(String groupName);
}
