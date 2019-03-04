package com.kms.katalon.execution.entity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.execution.constants.StringConstants;

public abstract class ExecutedEntity implements IExecutedEntity {

    private String id;

    private FileEntity entity;

    public FileEntity getEntity() {
        return entity;
    }

    protected ExecutedEntity() {
        entity = null;
    }

    protected ExecutedEntity(FileEntity entity) {
        updateEntity(entity);
    }

    protected void updateEntity(FileEntity entity) {
        this.entity = (FileEntity) entity.clone();
    }

    @Override
    public String getId() {
        if (id == null) {
            id = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        return id;
    }

    @Override
    public String getSourceName() {
        return entity.getName();
    }

    @Override
    public String getSourceId() {
        return entity.getIdForDisplay();
    }

    @Override
    public String getSourceDescription() {
        return entity.getDescription();
    }
    
    @Override
    public String getSourcePath() {
        return entity.getLocation();
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributes.put(StringConstants.ID.toLowerCase(), getSourceId());
        attributes.put(StringConstants.NAME.toLowerCase(), getSourceName());
        attributes.put(StringConstants.DESCRIPTION.toLowerCase(), getSourceDescription());
        return attributes;
    }

    @Override
    public Map<String, String> getCollectedDataInfo() {
        return Collections.emptyMap();
    }
}
