package com.kms.katalon.entity.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.IntegratedFileEntity;

public class ProjectEntity extends IntegratedFileEntity {
    private static final long serialVersionUID = 1L;

    private String UUID;

    private short pageLoadTimeout;

    private String folderLocation;

    private List<String> recentExpandedTreeEntityIds;

    private List<String> recentOpenedTreeEntityIds;
    
    private String migratedVersion;
    
    private SourceContent sourceContent = new SourceContent();
    
    private ProjectType type = ProjectType.GENERIC;
    
    private String projectFileLocation;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public short getPageLoadTimeout() {
        return this.pageLoadTimeout;
    }

    public void setPageLoadTimeout(short pageLoadTimeout) {
        this.pageLoadTimeout = pageLoadTimeout;
    }

    @Override
    public String getFileExtension() {
        return getProjectFileExtension();
    }

    public static String getProjectFileExtension() {
        return ".prj";
    }

    @Override
    public String getLocation() {
        if (getProjectFileLocation() == null) {
            return getFolderLocation() + File.separator + name + getFileExtension();
        } else {
            return getProjectFileLocation();
        }
    }

    public String getFolderLocation() {
        return folderLocation;
    }

    public void setFolderLocation(String folderLocation) {
        this.folderLocation = folderLocation;
    }

    public List<String> getRecentExpandedTreeEntityIds() {
        if (recentExpandedTreeEntityIds == null) {
            return new ArrayList<String>();
        }
        return recentExpandedTreeEntityIds;
    }

    public void setRecentExpandedTreeEntityIds(List<String> recentExpandedTreeEntityIds) {
        this.recentExpandedTreeEntityIds = recentExpandedTreeEntityIds;
    }

    public List<String> getRecentOpenedTreeEntityIds() {
        if (recentOpenedTreeEntityIds == null) {
            return new ArrayList<String>();
        }
        return recentOpenedTreeEntityIds;
    }

    public void setRecentOpenedTreeEntityIds(List<String> recentOpenedTreeEntityIds) {
        this.recentOpenedTreeEntityIds = recentOpenedTreeEntityIds;
    }

    public String getMigratedVersion() {
        return migratedVersion;
    }

    public void setMigratedVersion(String migratedVersion) {
        this.migratedVersion = migratedVersion;
    }

    public SourceContent getSourceContent() {
        return sourceContent;
    }

    public void setSourceContent(SourceContent sourceContent) {
        this.sourceContent = sourceContent;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public String getProjectFileLocation() {
        return projectFileLocation;
    }
    
    public void setProjectFileLocation(String projectFileLocation) {
        this.projectFileLocation = projectFileLocation;
    }
}