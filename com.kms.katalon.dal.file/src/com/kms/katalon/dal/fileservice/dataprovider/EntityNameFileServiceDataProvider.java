package com.kms.katalon.dal.fileservice.dataprovider;

import com.kms.katalon.dal.IEntityNameProvider;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.entity.folder.FolderEntity;

public class EntityNameFileServiceDataProvider implements IEntityNameProvider {

    @Override
    public String getAvailableName(String name, FolderEntity parentFolder, boolean isFolder) throws Exception {
       return EntityService.getInstance().getAvailableName(parentFolder.getId(), name, !isFolder);
    }

    @Override
    public void validateName(String name) throws Exception {
        EntityService.getInstance().validateName(name);
    }

}
