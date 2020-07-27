package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiProjectImportResult extends OpenApiImportNode {

    private FolderEntity projectFolder;

    private List<OpenApiRestServiceImportResult> restServiceImportResults = new ArrayList<>();

    public OpenApiProjectImportResult(FolderEntity folder) {
        this.projectFolder = folder;
    }

    @Override
    public FolderEntity getFileEntity() {
        return projectFolder;
    }

    public OpenApiRestServiceImportResult[] getServiceImportResults() {
        return restServiceImportResults.toArray(new OpenApiRestServiceImportResult[restServiceImportResults.size()]);
    }

    public OpenApiRestServiceImportResult newService(String name) {
        FolderEntity folder = newFolder(name, projectFolder);
        OpenApiRestServiceImportResult serviceResult = new OpenApiRestServiceImportResult(this, folder);
        restServiceImportResults.add(serviceResult);
        return serviceResult;
    }

    @Override
    public OpenApiImportNode[] getChildImportNodes() {
        return restServiceImportResults.toArray(new OpenApiImportNode[restServiceImportResults.size()]);
    }
}
