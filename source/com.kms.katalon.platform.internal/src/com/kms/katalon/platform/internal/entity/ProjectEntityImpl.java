package com.kms.katalon.platform.internal.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.katalon.platform.api.model.Integration;
import com.kms.katalon.entity.project.ProjectEntity;

public class ProjectEntityImpl implements com.katalon.platform.api.model.ProjectEntity {
    
    private final ProjectEntity source;
    
    public ProjectEntityImpl(ProjectEntity source) {
        this.source = source;
    }

    @Override
    public String getFileLocation() {
        return source.getId();
    }

    @Override
    public String getFolderLocation() {
        return source.getFolderLocation();
    }

    @Override
    public String getId() {
        return source.getId();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public List<Integration> getIntegrations() {
        return source.getIntegratedEntities().stream().map(i -> new IntegrationImpl(i)).collect(Collectors.toList());
    }

}
