package com.kms.katalon.usagetracking;

public class UsageInformation {
    private String email;

    private String version;

    private int projectCount = 0;

    private int testCaseCount = 0;

    private int testCaseRun = 0;

    private int newProjectCount = 0;

    private int newTestcaseCount = 0;

    private int newTestRunCount = 0;
    
    private int newProjectCreatedCount = 0;
    
    private int newTestCaseCreatedCount = 0;

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

    public int getTestCaseRunCount() {
        return testCaseRun;
    }

    public void setTestCaseRunCount(int testCaseRun) {
        this.testCaseRun = testCaseRun;
    }

    public int getNewProjectCount() {
        return newProjectCount;
    }

    public void setNewProjectCount(int newProjectCount) {
        this.newProjectCount = newProjectCount;
    }

    public int getNewTestCaseCount() {
        return newTestcaseCount;
    }

    public void setNewTestCaseCount(int newTestcaseCount) {
        this.newTestcaseCount = newTestcaseCount;
    }

    public int getNewTestRunCount() {
        return newTestRunCount;
    }

    public void setNewTestRunCount(int newTestRunCount) {
        this.newTestRunCount = newTestRunCount;
    }

    public int getNewProjectCreatedCount() {
        return newProjectCreatedCount;
    }

    public void setNewProjectCreatedCount(int newProjectCreatedCount) {
        this.newProjectCreatedCount = newProjectCreatedCount;
    }

    public int getNewTestCaseCreatedCount() {
        return newTestCaseCreatedCount;
    }

    public void setNewTestCaseCreatedCount(int newTestCaseCreatedCount) {
        this.newTestCaseCreatedCount = newTestCaseCreatedCount;
    }

}
