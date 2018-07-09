package com.kms.katalon.application.usagetracking;

import java.util.Map;

public class UsageInformation {
    public static String ANONYMOUS = "anonymous";

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

    private String sessionId;

    private String userKey;

    private String triggeredBy;

    private String runningMode;

    private Map<String, Object> extra;

    private UsageInformation() {
        // Disable default constructor
    }

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

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAnonymous() {
        return ANONYMOUS.equals(getEmail());
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getRunningMode() {
        return runningMode;
    }

    public void setRunningMode(String runningMode) {
        this.runningMode = runningMode;
    }

    public static UsageInformation createAnonymousInfo(String sessionId, String userKey) {
        UsageInformation anonymous = new UsageInformation();
        anonymous.setEmail(ANONYMOUS);
        anonymous.setSessionId(sessionId);
        anonymous.setUserKey(userKey);
        return anonymous;
    }

    public static UsageInformation createActivatedInfo(String email, String sessionId, String userKey) {
        UsageInformation activatedUser = new UsageInformation();
        activatedUser.setEmail(email);
        activatedUser.setSessionId(sessionId);
        activatedUser.setUserKey(userKey);
        return activatedUser;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
