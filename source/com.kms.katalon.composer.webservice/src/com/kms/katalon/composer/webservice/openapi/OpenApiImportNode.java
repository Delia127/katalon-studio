package com.kms.katalon.composer.webservice.openapi;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiImportNode {
    public FileEntity getFileEntity() {
        return null;
    }

    public OpenApiImportNode getParentImportNode() {
        return null;
    }

    public OpenApiImportNode[] getChildImportNodes() {
        return new OpenApiImportNode[0];
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
