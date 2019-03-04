package com.kms.katalon.platform.internal.entity;

import java.io.File;

import com.kms.katalon.entity.file.SystemFileEntity;


public class SystemFileEntityImpl implements com.katalon.platform.api.model.SystemFileEntity {
    
    private final SystemFileEntity source;
    
    public SystemFileEntityImpl(SystemFileEntity source) {
        this.source = source;
    }

    @Override
    public String getFileLocation() {
        return source.getId() + source.getFileExtension();
    }

    @Override
    public String getFolderLocation() {
        return source.getParentFolder().getId();
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
    public File getFile() {
        return source.getFile();
    }

}
