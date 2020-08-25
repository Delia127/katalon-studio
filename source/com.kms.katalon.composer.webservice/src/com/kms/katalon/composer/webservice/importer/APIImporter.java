package com.kms.katalon.composer.webservice.importer;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.entity.folder.FolderEntity;

public class APIImporter {

    protected FolderEntity getRootImportFolder(String name, FolderEntity parentFolder) throws Exception {
        if (StringUtils.isBlank(name)) {
            name = "Imported APIs";
        }
        name = toValidFileName(name);
        name = EntityNameController.getInstance().getAvailableName(name, parentFolder, true);
        FolderEntity folder = new FolderEntity();
        folder.setName(name);
        folder.setParentFolder(parentFolder);
        folder.setProject(parentFolder.getProject());
        folder.setFolderType(parentFolder.getFolderType());
        folder.setDescription("folder");
        return folder;
    }
    
    protected String toValidFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_\\.\\s]", "_");
    }
}
