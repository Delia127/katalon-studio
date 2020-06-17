package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class SoapUIRestServiceImportResult extends SoapUIImportNode {

    private SoapUIProjectImportResult projectImportResult;

    private FolderEntity serviceFolder;

    private List<SoapUIRestResourceImportResult> resourceImportResults = new ArrayList<>();

    private Set<String> resourceFolderNames = new HashSet<>();
    
    private String basePath;

    public SoapUIRestServiceImportResult(SoapUIProjectImportResult projectImportResult, FolderEntity serviceFolder) {
        this.projectImportResult = projectImportResult;
        this.serviceFolder = serviceFolder;
    }

    public SoapUIProjectImportResult getProjectImportResult() {
        return projectImportResult;
    }

    public SoapUIRestResourceImportResult[] getResourceImportResults() {
        return resourceImportResults.toArray(new SoapUIRestResourceImportResult[resourceImportResults.size()]);
    }

    public SoapUIRestResourceImportResult newResource(String name, String path) {
        if (!isResourceFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Resource folder name already exists.");
        }
        resourceFolderNames.add(name);

        FolderEntity folder = newFolder(name, serviceFolder);
        SoapUIRestResourceImportResult resourceResult = new SoapUIRestResourceImportResult(this, path, folder);
        resourceImportResults.add(resourceResult);
        return resourceResult;
    }

    public boolean isResourceFolderNameAvailable(String folderName) {
        return !resourceFolderNames.contains(folderName);
    }

    protected String getBasePath() {
        return basePath;
    }

    protected void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public FileEntity getFileEntity() {
        return serviceFolder;
    }

    @Override
    public SoapUIImportNode getParentImportNode() {
        return projectImportResult;
    }

    @Override
    public SoapUIImportNode[] getChildImportNodes() {
        return resourceImportResults.toArray(new SoapUIImportNode[resourceImportResults.size()]);
    }
}
