package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiRestServiceImportResult extends OpenApiImportNode {

    private OpenApiProjectImportResult projectImportResult;

    private FolderEntity serviceFolder;

    private List<OpenApiRestResourceImportResult> resourceImportResults = new ArrayList<>();

    private String basePath;

    public OpenApiRestServiceImportResult(OpenApiProjectImportResult projectImportResult, FolderEntity serviceFolder) {
        this.projectImportResult = projectImportResult;
        this.serviceFolder = serviceFolder;
    }

    public OpenApiProjectImportResult getProjectImportResult() {
        return projectImportResult;
    }

    public OpenApiRestResourceImportResult[] getResourceImportResults() {
        return resourceImportResults.toArray(new OpenApiRestResourceImportResult[resourceImportResults.size()]);
    }

    public OpenApiRestResourceImportResult newResource(String name, String path) {
        FolderEntity folder = newFolder(name, serviceFolder);
        OpenApiRestResourceImportResult resourceResult = new OpenApiRestResourceImportResult(this, path, folder);
        resourceImportResults.add(resourceResult);
        return resourceResult;
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
    public OpenApiImportNode getParentImportNode() {
        return projectImportResult;
    }

    @Override
    public OpenApiImportNode[] getChildImportNodes() {
        return resourceImportResults.toArray(new OpenApiImportNode[resourceImportResults.size()]);
    }
}
