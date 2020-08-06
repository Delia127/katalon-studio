package com.kms.katalon.composer.webservice.importing.model;

import java.util.List;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public abstract class RestImportNode {

    public abstract FileEntity getFileEntity();
    
    public abstract RestImportNode getParentImportNode();
    
    public abstract List<RestImportNode> getChildImportNodes();
    
    protected FolderEntity newFolder(String name, FolderEntity parentFolder) {
        FolderEntity folder = new FolderEntity();
        folder.setName(name);
        folder.setParentFolder(parentFolder);
        folder.setProject(parentFolder.getProject());
        folder.setFolderType(parentFolder.getFolderType());
        folder.setDescription("folder");
        return folder;
    }
}
