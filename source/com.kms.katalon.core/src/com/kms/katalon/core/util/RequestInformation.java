package com.kms.katalon.core.util;

public class RequestInformation {

    private String id;
    
    private String testObjectId;
    
    private String testCaseId;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTestObjectId() {
        return testObjectId;
    }
    
    public void setTestObjectId(String testObjectId) {
        this.testObjectId = testObjectId;
    }
    
    public String getTestCaseId() {
        return testCaseId;
    }
    
    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }
}
