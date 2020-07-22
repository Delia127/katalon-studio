package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsRunScheduler {
    private long id;

    private String name;

    private Date startTime;

    private Date endTime;

    private Date nextTime;

    private boolean active;

    private int interval;

    private String intervalUnit;

    private long runConfigurationId;

    private AnalyticsRunConfiguration runConfiguration;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getRunConfigurationId() {
        return runConfigurationId;
    }

    public void setRunConfigurationId(long runConfigurationId) {
        this.runConfigurationId = runConfigurationId;
    }

    public AnalyticsRunConfiguration getRunConfiguration() {
        return runConfiguration;
    }

    public void setRunConfiguration(AnalyticsRunConfiguration runConfiguration) {
        this.runConfiguration = runConfiguration;
    }

}
