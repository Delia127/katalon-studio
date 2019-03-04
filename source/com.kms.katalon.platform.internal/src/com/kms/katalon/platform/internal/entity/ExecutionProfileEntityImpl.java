package com.kms.katalon.platform.internal.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfileEntityImpl implements com.katalon.platform.api.model.ExecutionProfileEntity {

    private final ExecutionProfileEntity source;

    public ExecutionProfileEntityImpl(ExecutionProfileEntity source) {
        this.source = source;
    }

    @Override
    public String getFileLocation() {
        return source.getLocation();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getLocation();
    }

    @Override
    public String getId() {
        return source.getIdForDisplay();
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public List<com.katalon.platform.api.model.VariableEntity> getVariables() {
        return source.getGlobalVariableEntities()
                .stream()
                .map(v -> new GlobalVariableEntityImpl(v))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDefaultProfile() {
        return source.isDefaultProfile();
    }

}
