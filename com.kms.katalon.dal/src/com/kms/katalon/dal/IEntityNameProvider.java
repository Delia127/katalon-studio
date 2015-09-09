package com.kms.katalon.dal;

import com.kms.katalon.entity.folder.FolderEntity;

public interface IEntityNameProvider {
    public String getAvailableName(String name, FolderEntity parentFolder, boolean isFolder) throws Exception;

    public void validateName(String name) throws Exception;
}
