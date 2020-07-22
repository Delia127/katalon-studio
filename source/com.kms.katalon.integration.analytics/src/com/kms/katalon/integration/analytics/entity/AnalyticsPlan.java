package com.kms.katalon.integration.analytics.entity;

public class AnalyticsPlan {
    private long id;

    private String name;

    private String command;

    private long projectId;

    private long teamId;

    private long testProjectId;

    private long testSuiteCollectionId;

    private String configType;

    private AnalyticsPlanTestProject testProject;

    private AnalyticsAgent[] agents;

    private String cloudType;

    private AnalyticsJob latestJob;

    private AnalyticsRunScheduler nextRunScheduler;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getTestProjectId() {
        return testProjectId;
    }

    public void setTestProjectId(long testProjectId) {
        this.testProjectId = testProjectId;
    }

    public long getTestSuiteCollectionId() {
        return testSuiteCollectionId;
    }

    public void setTestSuiteCollectionId(long testSuiteCollectionId) {
        this.testSuiteCollectionId = testSuiteCollectionId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public AnalyticsPlanTestProject getTestProject() {
        return testProject;
    }

    public void setTestProject(AnalyticsPlanTestProject testProject) {
        this.testProject = testProject;
    }

    public AnalyticsAgent[] getAgents() {
        return agents;
    }

    public void setAgents(AnalyticsAgent[] agents) {
        this.agents = agents;
    }

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    public AnalyticsJob getLatestJob() {
        return latestJob;
    }

    public void setLatestJob(AnalyticsJob latestJob) {
        this.latestJob = latestJob;
    }

    public AnalyticsRunScheduler getNextRunScheduler() {
        return nextRunScheduler;
    }

    public void setNextRunScheduler(AnalyticsRunScheduler nextRunScheduler) {
        this.nextRunScheduler = nextRunScheduler;
    }

}
