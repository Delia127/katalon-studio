package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsJob {
    private long id;

    private int buildNumber;

    private AnalyticsJobStatus status;

    private Date queueAt;

    private Date startTime;

    private Date triggerAt;

    private AnalyticsPlanTestProject testProject;

    private AnalyticsExecution execution;

    private AnalyticsAgent agent;

    private AnalyticsRunConfiguration runConfiguration;

    private AnalyticsUser user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public AnalyticsJobStatus getStatus() {
        return status;
    }

    public void setStatus(AnalyticsJobStatus status) {
        this.status = status;
    }

    public Date getQueueAt() {
        return queueAt;
    }

    public void setQueueAt(Date queueAt) {
        this.queueAt = queueAt;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public AnalyticsPlanTestProject getTestProject() {
        return testProject;
    }

    public void setTestProject(AnalyticsPlanTestProject testProject) {
        this.testProject = testProject;
    }

    public AnalyticsExecution getExecution() {
        return execution;
    }

    public void setExecution(AnalyticsExecution execution) {
        this.execution = execution;
    }

    public AnalyticsAgent getAgent() {
        return agent;
    }

    public void setAgent(AnalyticsAgent agent) {
        this.agent = agent;
    }

    public AnalyticsRunConfiguration getRunConfiguration() {
        return runConfiguration;
    }

    public void setRunConfiguration(AnalyticsRunConfiguration runConfiguration) {
        this.runConfiguration = runConfiguration;
    }

    public AnalyticsUser getUser() {
        return user;
    }

    public void setUser(AnalyticsUser user) {
        this.user = user;
    }

    public Date getTriggerAt() {
        return triggerAt;
    }

    public void setTriggerAt(Date triggerAt) {
        this.triggerAt = triggerAt;
    }

    public enum AnalyticsJobStatus {
        QUEUED, RUNNING, FAILED, SUCCESS, CANCELED, ERROR, WAIT_FOR_TRIGGER
    }

}
