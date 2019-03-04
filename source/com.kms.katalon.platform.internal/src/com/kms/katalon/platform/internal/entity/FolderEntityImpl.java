package com.kms.katalon.platform.internal.entity;

import com.kms.katalon.entity.folder.FolderEntity;

public class FolderEntityImpl implements com.katalon.platform.api.model.FolderEntity {
    
    private final FolderEntity source;
    
    public FolderEntityImpl(FolderEntity source) {
        this.source = source;
    }

    @Override
    public String getFileLocation() {
        return source.getId();
    }

    @Override
    public String getFolderLocation() {
        return source.getId();
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
    public String getParentFolderId() {
        return source.getParentFolder() != null ? source.getParentFolder().getIdForDisplay() : null;
    }

}
