package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiProjectImportResult extends OpenApiImportNode {

    private FolderEntity projectFolder;

    private String basePath;

    private List<OpenApiRestResourceImportResult> resourceImportResults = new ArrayList<>();

    public OpenApiProjectImportResult(FolderEntity folder, String basePath) {
        this.projectFolder = folder;
        this.basePath = basePath;
    }

    @Override
    public FolderEntity getFileEntity() {
        return projectFolder;
    }

    protected String getBasePath() {
        return basePath;
    }

    public OpenApiRestResourceImportResult newResource(String name, String path) {
        FolderEntity folder = newFolder(name, projectFolder);
        OpenApiRestResourceImportResult resourceResult = new OpenApiRestResourceImportResult(this, path, folder);
        resourceImportResults.add(resourceResult);
        return resourceResult;
    }

    @Override
    public OpenApiImportNode[] getChildImportNodes() {
        return resourceImportResults.toArray(new OpenApiImportNode[resourceImportResults.size()]);
    }
}
