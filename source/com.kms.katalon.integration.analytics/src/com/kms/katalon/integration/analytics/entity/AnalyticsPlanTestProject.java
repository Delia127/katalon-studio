package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsPlanTestProject {
    private long id;

    private String name;

    private long uploadFileId;

    private long projectId;

    private Date createdAt;

    private String type;

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

    public long getUploadFileId() {
        return uploadFileId;
    }

    public void setUploadFileId(long uploadFileId) {
        this.uploadFileId = uploadFileId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
