package com.kms.katalon.usagetracking;


public class UsageInformation {
    private String email;
    private String version;
    private int projectCount = 0;
    private int testCaseCount = 0;
    private int testCaseRun = 0;
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public int getProjectCount() {
        return projectCount;
    }
    public void setProjectCount(int projectCount) {
        this.projectCount = projectCount;
    }
    public int getTestCaseCount() {
        return testCaseCount;
    }
    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }
    public int getTestCaseRun() {
        return testCaseRun;
    }
    public void setTestCaseRun(int testCaseRun) {
        this.testCaseRun = testCaseRun;
    }
    
    
}
