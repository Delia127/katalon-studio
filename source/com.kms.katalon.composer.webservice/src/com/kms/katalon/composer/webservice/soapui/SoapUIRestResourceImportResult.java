package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class SoapUIRestResourceImportResult extends SoapUIRestResourceImportNode {

    private SoapUIRestServiceImportResult serviceImportResult;

    private SoapUIRestResourceImportResult parentResourceImportResult;

    private String path;
    
    private FolderEntity resourceFolder;

    private List<SoapUIRestMethodImportResult> methodImportResults = new ArrayList<>();

    private List<SoapUIRestResourceImportResult> resourceImportResults = new ArrayList<>();

    private Set<String> childFolderNames = new HashSet<>();

    public SoapUIRestResourceImportResult(SoapUIRestServiceImportResult serviceImportResult, String path,
            FolderEntity folder) {
        this.serviceImportResult = serviceImportResult;
        this.path = path;
        this.resourceFolder = folder;
    }

    public SoapUIRestResourceImportResult(SoapUIRestResourceImportResult resourceImportResult, String path,
            FolderEntity folder) {
        this.parentResourceImportResult = resourceImportResult;
        this.path = path;
        this.resourceFolder = folder;
    }

    protected SoapUIRestServiceImportResult getServiceImportResult() {
        return serviceImportResult;
    }

    public String getPath() {
        return path;
    }

    protected SoapUIRestResourceImportResult getParentResourceImportResult() {
        return parentResourceImportResult;
    }

    public SoapUIRestMethodImportResult[] getMethodImportResults() {
        return methodImportResults.toArray(new SoapUIRestMethodImportResult[methodImportResults.size()]);
    }

    public SoapUIRestMethodImportResult newMethod(String name, String httpMethod) {
        if (!isChildResultFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Method folder name already exists.");
        }
        childFolderNames.add(name);
        FolderEntity folder = newFolder(name, resourceFolder);
        SoapUIRestMethodImportResult methodResult = new SoapUIRestMethodImportResult(this, httpMethod, folder);
        methodImportResults.add(methodResult);
        return methodResult;
    }

    public SoapUIRestResourceImportResult[] getResourceImportResults() {
        return resourceImportResults.toArray(new SoapUIRestResourceImportResult[resourceImportResults.size()]);
    }

    public SoapUIRestResourceImportResult newResource(String name, String path) {
        if (!isChildResultFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Resource folder name already exists.");
        }
        childFolderNames.add(name);
        FolderEntity folder = newFolder(name, resourceFolder);
        SoapUIRestResourceImportResult resourceResult = new SoapUIRestResourceImportResult(this, path, folder);
        resourceImportResults.add(resourceResult);
        return resourceResult;
    }

    public boolean isChildResultFolderNameAvailable(String folderName) {
        return !childFolderNames.contains(folderName);
    }

    @Override
    public FileEntity getFileEntity() {
        return resourceFolder;
    }

    @Override
    public SoapUIImportNode getParentImportNode() {
        if (parentResourceImportResult != null) {
            return parentResourceImportResult;
        }
        return serviceImportResult;
    }

    @Override
    public SoapUIImportNode[] getChildImportNodes() {
        List<SoapUIImportNode> childImportNodes = new ArrayList<>();
        childImportNodes.addAll(methodImportResults);
        childImportNodes.addAll(resourceImportResults);
        return childImportNodes.toArray(new SoapUIImportNode[childImportNodes.size()]);
    }
}