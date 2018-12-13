package com.kms.katalon.tracking.model;

public class ProjectStatistics {
    
    private String projectId;
    
    private int testCaseCount = 0;
    
    private int jiraIntegratedTestCaseCount = 0;
    
    private int webTestStepCount = 0;
    
    private int apiTestStepCount = 0;
    
    private int mobileTestStepCount = 0;
    
    private int customKeywordTestStepCount = 0;
    
    private int callTestCaseTestStepCount = 0;
    
    private int totalTestStepCount = 0;
    
    private int webTestObjectCount = 0;
    
    private int apiTestObjectCount = 0;
    
    private int testSuiteCount = 0;
    
    private int testCaseInTestSuiteCount = 0;
    
    private int testSuiteCollectionCount = 0;
    
    private int csvDataFileCount = 0;
    
    private int excelDataFileCount = 0;
    
    private int databaseDataFileCount = 0;
    
    private int internalDataFileCount = 0;
    
    private int checkpointCount = 0;

    private int customKeywordCount = 0;

    private int testListenerCount = 0;
    
    private int reportCount = 0;
    
    private int executionCount = 0;

    private int profileCount = 0;
    
    private int featureFileCount = 0;
    
    private int groovyScriptFileCount = 0;
    
    private boolean gitIntegrated = false;
    
    private boolean jiraIntegrated = false;
    
    private boolean kobitonIntegrated = false;
    
    private boolean slackIntegrated = false;
    
    private boolean qTestIntegrated = false;
    
    private boolean katalonAnalyticsIntegrated = false;
    
    private boolean remoteWebDriverConfigured = false;
    
    private boolean continueOnFailure = false;
    
    private String webLocatorConfig;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public int getJiraIntegratedTestCaseCount() {
        return jiraIntegratedTestCaseCount;
    }

    public void setJiraIntegratedTestCaseCount(int jiraIntegratedTestCaseCount) {
        this.jiraIntegratedTestCaseCount = jiraIntegratedTestCaseCount;
    }

    public int getWebTestStepCount() {
        return webTestStepCount;
    }

    public void setWebTestStepCount(int webTestStepCount) {
        this.webTestStepCount = webTestStepCount;
    }

    public int getApiTestStepCount() {
        return apiTestStepCount;
    }

    public void setApiTestStepCount(int apiTestStepCount) {
        this.apiTestStepCount = apiTestStepCount;
    }

    public int getMobileTestStepCount() {
        return mobileTestStepCount;
    }

    public void setMobileTestStepCount(int mobileTestStepCount) {
        this.mobileTestStepCount = mobileTestStepCount;
    }

    public int getCustomKeywordTestStepCount() {
        return customKeywordTestStepCount;
    }

    public void setCustomKeywordTestStepCount(int customKeywordTestStepCount) {
        this.customKeywordTestStepCount = customKeywordTestStepCount;
    }

    public int getCallTestCaseTestStepCount() {
        return callTestCaseTestStepCount;
    }

    public void setCallTestCaseTestStepCount(int callTestCaseTestStepCount) {
        this.callTestCaseTestStepCount = callTestCaseTestStepCount;
    }
    
    public int getTotalTestStepCount() {
        return totalTestStepCount;
    }

    public void setTotalTestStepCount(int totalTestStepCount) {
        this.totalTestStepCount = totalTestStepCount;
    }

    public int getWebTestObjectCount() {
        return webTestObjectCount;
    }

    public void setWebTestObjectCount(int webTestObjectCount) {
        this.webTestObjectCount = webTestObjectCount;
    }

    public int getApiTestObjectCount() {
        return apiTestObjectCount;
    }

    public void setApiTestObjectCount(int apiTestObjectCount) {
        this.apiTestObjectCount = apiTestObjectCount;
    }

    public int getTestSuiteCount() {
        return testSuiteCount;
    }

    public void setTestSuiteCount(int testSuiteCount) {
        this.testSuiteCount = testSuiteCount;
    }

    public int getTestCaseInTestSuiteCount() {
        return testCaseInTestSuiteCount;
    }

    public void setTestCaseInTestSuiteCount(int testCaseInTestSuiteCount) {
        this.testCaseInTestSuiteCount = testCaseInTestSuiteCount;
    }
    
    public int getTestSuiteCollectionCount() {
        return testSuiteCollectionCount;
    }

    public void setTestSuiteCollectionCount(int testSuiteCollectionCount) {
        this.testSuiteCollectionCount = testSuiteCollectionCount;
    }

    public int getCsvDataFileCount() {
        return csvDataFileCount;
    }

    public void setCsvDataFileCount(int csvDataFileCount) {
        this.csvDataFileCount = csvDataFileCount;
    }

    public int getExcelDataFileCount() {
        return excelDataFileCount;
    }

    public void setExcelDataFileCount(int excelDataFileCount) {
        this.excelDataFileCount = excelDataFileCount;
    }

    public int getDatabaseDataFileCount() {
        return databaseDataFileCount;
    }

    public void setDatabaseDataFileCount(int databaseDataFileCount) {
        this.databaseDataFileCount = databaseDataFileCount;
    }

    public int getInternalDataFileCount() {
        return internalDataFileCount;
    }

    public void setInternalDataFileCount(int internalDataFileCount) {
        this.internalDataFileCount = internalDataFileCount;
    }

    public int getCheckpointCount() {
        return checkpointCount;
    }

    public void setCheckpointCount(int checkpointCount) {
        this.checkpointCount = checkpointCount;
    }

    public int getCustomKeywordCount() {
        return customKeywordCount;
    }

    public void setCustomKeywordCount(int customKeywordCount) {
        this.customKeywordCount = customKeywordCount;
    }

    public int getTestListenerCount() {
        return testListenerCount;
    }

    public void setTestListenerCount(int testListenerCount) {
        this.testListenerCount = testListenerCount;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public int getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(int executionCount) {
        this.executionCount = executionCount;
    }

    public int getProfileCount() {
        return profileCount;
    }

    public void setProfileCount(int profileCount) {
        this.profileCount = profileCount;
    }

    public int getFeatureFileCount() {
        return featureFileCount;
    }

    public void setFeatureFileCount(int featureFileCount) {
        this.featureFileCount = featureFileCount;
    }

    public int getGroovyScriptFileCount() {
        return groovyScriptFileCount;
    }

    public void setGroovyScriptFileCount(int groovyScriptFileCount) {
        this.groovyScriptFileCount = groovyScriptFileCount;
    }

    public boolean isGitIntegrated() {
        return gitIntegrated;
    }

    public void setGitIntegrated(boolean gitIntegrated) {
        this.gitIntegrated = gitIntegrated;
    }

    public boolean isJiraIntegrated() {
        return jiraIntegrated;
    }

    public void setJiraIntegrated(boolean jiraIntegrated) {
        this.jiraIntegrated = jiraIntegrated;
    }

    public boolean isKobitonIntegrated() {
        return kobitonIntegrated;
    }

    public void setKobitonIntegrated(boolean kobitonIntegrated) {
        this.kobitonIntegrated = kobitonIntegrated;
    }

    public boolean isSlackIntegrated() {
        return slackIntegrated;
    }

    public void setSlackIntegrated(boolean slackIntegrated) {
        this.slackIntegrated = slackIntegrated;
    }

    public boolean isqTestIntegrated() {
        return qTestIntegrated;
    }

    public void setqTestIntegrated(boolean qTestIntegrated) {
        this.qTestIntegrated = qTestIntegrated;
    }
    
    public boolean isKatalonAnalyticsIntegrated() {
        return katalonAnalyticsIntegrated;
    }

    public void setKatalonAnalyticsIntegrated(boolean katalonAnalyticsIntegrated) {
        this.katalonAnalyticsIntegrated = katalonAnalyticsIntegrated;
    }

    public boolean isRemoteWebDriverConfigured() {
        return remoteWebDriverConfigured;
    }

    public void setRemoteWebDriverConfigured(boolean remoteWebDriverConfigured) {
        this.remoteWebDriverConfigured = remoteWebDriverConfigured;
    }
    
    public boolean isContinueOnFailure() {
        return continueOnFailure;
    }

    public void setContinueOnFailure(boolean continueOnFailure) {
        this.continueOnFailure = continueOnFailure;
    }

    public String getWebLocatorConfig() {
        return webLocatorConfig;
    }

    public void setWebLocatorConfig(String webLocatorConfig) {
        this.webLocatorConfig = webLocatorConfig;
    }

}
