package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiRestResourceImportResult extends OpenApiRestResourceImportNode {

    private OpenApiProjectImportResult projectImportResult;

    private String path;

    private FolderEntity resourceFolder;

    private List<OpenApiRestRequestImportResult> requestImportResults = new ArrayList<>();

    public OpenApiRestResourceImportResult(OpenApiProjectImportResult projectImportResult, String path,
            FolderEntity folder) {
        this.projectImportResult = projectImportResult;
        this.path = path;
        this.resourceFolder = folder;
    }

    public String getPath() {
        return path;
    }

    public OpenApiRestRequestImportResult newRequest(String name) {
        OpenApiRestRequestImportResult requestResult = new OpenApiRestRequestImportResult(this, name);
        requestImportResults.add(requestResult);
        return requestResult;
    }

    @Override
    public FileEntity getFileEntity() {
        return resourceFolder;
    }

    @Override
    public OpenApiImportNode getParentImportNode() {
        return projectImportResult;
    }

    @Override
    public OpenApiImportNode[] getChildImportNodes() {
        List<OpenApiImportNode> childImportNodes = new ArrayList<>();
        childImportNodes.addAll(requestImportResults);
        return childImportNodes.toArray(new OpenApiImportNode[childImportNodes.size()]);
    }
}
