package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsTracking {

    private Long organizationId;

    private String machineId;

    private String sessionId;

    private Date startTime;

    private Date endTime;

    private String ksVersion;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getKsVersion() {
        return ksVersion;
    }

    public void setKsVersion(String ksVersion) {
        this.ksVersion = ksVersion;
    }
    
    
}
