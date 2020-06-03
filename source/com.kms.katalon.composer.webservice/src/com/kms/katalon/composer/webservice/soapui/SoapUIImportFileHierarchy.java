package com.kms.katalon.composer.webservice.soapui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.entity.folder.FolderEntity;

public class SoapUIImportFileHierarchy {

    private Map<String, FolderEntity> foldersByPaths = new HashMap<>();

    private FolderEntity rootFolder;

    public SoapUIImportFileHierarchy(FolderEntity rootFolder) {
        this.rootFolder = rootFolder;
        foldersByPaths.put("/", rootFolder);
    }

    public FolderEntity getFolderByPath(String folderPath) {
        String[] pathSegments = StringUtils.split(folderPath, "/");
        FolderEntity folder = rootFolder;
        String path = "";
        for (String pathSegment : pathSegments) {
            String pathKey = path + "/" + pathSegment;
            if (foldersByPaths.containsKey(pathKey)) {
                folder = foldersByPaths.get(pathKey);
            } else {
                FolderEntity newFolder = new FolderEntity();
                newFolder.setName(pathSegment);
                newFolder.setParentFolder(folder);
                foldersByPaths.put(pathKey, newFolder);
                folder = newFolder;
            }
        }
        return folder;
    }
}