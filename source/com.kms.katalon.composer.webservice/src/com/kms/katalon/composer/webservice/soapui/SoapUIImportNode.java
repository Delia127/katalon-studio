package com.kms.katalon.composer.webservice.soapui;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public abstract class SoapUIImportNode {

    public FileEntity getFileEntity() {
        return null;
    }
    
    public SoapUIImportNode getParentImportNode() {
        return null;
    }
    
    public SoapUIImportNode[] getChildImportNodes() {
        return new SoapUIImportNode[0];             
    }
    
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
