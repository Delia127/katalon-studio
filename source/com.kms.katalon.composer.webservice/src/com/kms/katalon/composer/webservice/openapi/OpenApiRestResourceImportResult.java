package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiRestResourceImportResult extends OpenApiRestResourceImportNode {

    private OpenApiRestServiceImportResult serviceImportResult;

    private String path;

    private FolderEntity resourceFolder;

    private List<OpenApiRestMethodImportResult> methodImportResults = new ArrayList<>();

    public OpenApiRestResourceImportResult(OpenApiRestServiceImportResult serviceImportResult, String path,
            FolderEntity folder) {
        this.serviceImportResult = serviceImportResult;
        this.path = path;
        this.resourceFolder = folder;
    }

    protected OpenApiRestServiceImportResult getServiceImportResult() {
        return serviceImportResult;
    }

    public String getPath() {
        return path;
    }

    public OpenApiRestMethodImportResult[] getMethodImportResults() {
        return methodImportResults.toArray(new OpenApiRestMethodImportResult[methodImportResults.size()]);
    }

    public OpenApiRestMethodImportResult newMethod(String name, String httpMethod) {
        FolderEntity folder = newFolder(name, resourceFolder);
        OpenApiRestMethodImportResult methodResult = new OpenApiRestMethodImportResult(this, httpMethod, folder);
        methodImportResults.add(methodResult);
        return methodResult;
    }

    @Override
    public FileEntity getFileEntity() {
        return resourceFolder;
    }

    @Override
    public OpenApiImportNode getParentImportNode() {
        return serviceImportResult;
    }

    @Override
    public OpenApiImportNode[] getChildImportNodes() {
        List<OpenApiImportNode> childImportNodes = new ArrayList<>();
        childImportNodes.addAll(methodImportResults);
        return childImportNodes.toArray(new OpenApiImportNode[childImportNodes.size()]);
    }
}