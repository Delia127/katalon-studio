package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsAgent {
    private long id;

    private String name;

    private String ip;

    private String uuid;

    private Date lastPing;

    private String os;

    private long teamId;

    private String hostName;

    private boolean active;

    private long threshold;

    private int numExecutingJobs;

    private int numAssignedJobs;

    private String agenttVersion;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getLastPing() {
        return lastPing;
    }

    public void setLastPing(Date lastPing) {
        this.lastPing = lastPing;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getThreshold() {
        return threshold;
    }

    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    public int getNumExecutingJobs() {
        return numExecutingJobs;
    }

    public void setNumExecutingJobs(int numExecutingJobs) {
        this.numExecutingJobs = numExecutingJobs;
    }

    public int getNumAssignedJobs() {
        return numAssignedJobs;
    }

    public void setNumAssignedJobs(int numAssignedJobs) {
        this.numAssignedJobs = numAssignedJobs;
    }

    public String getAgenttVersion() {
        return agenttVersion;
    }

    public void setAgenttVersion(String agenttVersion) {
        this.agenttVersion = agenttVersion;
    }

}
