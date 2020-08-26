package com.kms.katalon.composer.webservice.importing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class RestResourceImportResult extends RestResourceImportNode {

    private RestServiceImportResult serviceImportResult;

    private RestResourceImportResult parentResourceImportResult;

    private String path;
    
    private FolderEntity resourceFolder;

    private List<RestMethodImportResult> methodImportResults = new ArrayList<>();

    private List<RestResourceImportResult> childResourceImportResults = new ArrayList<>();

    private Set<String> childFolderNames = new HashSet<>();

    public RestResourceImportResult(RestServiceImportResult serviceImportResult, String path,
            FolderEntity folder) {
        this.serviceImportResult = serviceImportResult;
        this.path = path;
        this.resourceFolder = folder;
    }

    public RestResourceImportResult(RestResourceImportResult resourceImportResult, String path,
            FolderEntity folder) {
        this.parentResourceImportResult = resourceImportResult;
        this.path = path;
        this.resourceFolder = folder;
    }

    protected RestServiceImportResult getServiceImportResult() {
        return serviceImportResult;
    }

    public String getPath() {
        StringBuilder pathBuilder = new StringBuilder();
        if (parentResourceImportResult != null && parentResourceImportResult.getPath() != null) {
            pathBuilder.append(parentResourceImportResult.getPath());
        }
        if (StringUtils.isNotBlank(path)) {
            if (!path.startsWith("/")) {
                pathBuilder.append("/");
            }
            pathBuilder.append(path);
        }
        return pathBuilder.toString();
    }

    protected RestResourceImportResult getParentResourceImportResult() {
        return parentResourceImportResult;
    }

    public List<RestMethodImportResult> getMethodImportResults() {
        return Collections.unmodifiableList(methodImportResults);
    }

    public RestMethodImportResult newMethod(String name, String httpMethod) {
        if (!isChildResultFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Method folder name already exists.");
        }
        childFolderNames.add(name);
        FolderEntity folder = newFolder(name, resourceFolder);
        RestMethodImportResult methodResult = new RestMethodImportResult(this, httpMethod, folder);
        methodImportResults.add(methodResult);
        return methodResult;
    }

    public List<RestResourceImportResult> getChildResourceImportResults() {
        return Collections.unmodifiableList(childResourceImportResults);
    }

    public RestResourceImportResult newResource(String name, String path) {
        if (!isChildResultFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Resource folder name already exists.");
        }
        childFolderNames.add(name);
        FolderEntity folder = newFolder(name, resourceFolder);
        RestResourceImportResult resourceResult = new RestResourceImportResult(this, path, folder);
        getParameters().stream().forEach(p -> {
            RestParameterImportResult pr = resourceResult.addNewParameter(p.getName());
            pr.setValue(p.getValue());
            pr.setStyle(p.getStyle());
        });
        childResourceImportResults.add(resourceResult);
        return resourceResult;
    }

    public boolean isChildResultFolderNameAvailable(String folderName) {
        return !childFolderNames.contains(folderName);
    }

    public FileEntity getFileEntity() {
        return resourceFolder;
    }

    public RestImportNode getParentImportNode() {
        if (parentResourceImportResult != null) {
            return parentResourceImportResult;
        }
        return serviceImportResult;
    }

    public List<RestImportNode> getChildImportNodes() {
        List<RestImportNode> childImportNodes = new ArrayList<>();
        childImportNodes.addAll(methodImportResults);
        childImportNodes.addAll(childResourceImportResults);
        return Collections.unmodifiableList(childImportNodes);
    }
}