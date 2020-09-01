package com.kms.katalon.composer.webservice.importing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class RestServiceImportResult extends RestImportNode {

    private RestImportNode parentImportNode;

    private FolderEntity serviceFolder;

    private List<RestResourceImportResult> resourceImportResults = new ArrayList<>();

    private Set<String> resourceFolderNames = new HashSet<>();
    
    private String endpoint;
    
    private String basePath;

    public RestServiceImportResult(RestImportNode parentImportNode, FolderEntity serviceFolder) {
        this.parentImportNode = parentImportNode;
        this.serviceFolder = serviceFolder;
    }

    public List<RestResourceImportResult> getResourceImportResults() {
        return Collections.unmodifiableList(resourceImportResults);
    }

    public RestResourceImportResult newResource(String name, String path) {
        if (!isResourceFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Resource folder name already exists.");
        }
        resourceFolderNames.add(name);

        FolderEntity folder = newFolder(name, serviceFolder);
        RestResourceImportResult resourceResult = new RestResourceImportResult(this, path, folder);
        resourceImportResults.add(resourceResult);
        return resourceResult;
    }

    public boolean isResourceFolderNameAvailable(String folderName) {
        return !resourceFolderNames.contains(folderName);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public FileEntity getFileEntity() {
        return serviceFolder;
    }

    public RestImportNode getParentImportNode() {
        return parentImportNode;
    }

    public List<RestImportNode> getChildImportNodes() {
        return Collections.unmodifiableList(resourceImportResults);
    }
}
