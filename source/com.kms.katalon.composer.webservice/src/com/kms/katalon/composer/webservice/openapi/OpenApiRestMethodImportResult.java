package com.kms.katalon.composer.webservice.openapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.folder.FolderEntity;

public class OpenApiRestMethodImportResult extends OpenApiRestResourceImportNode {

    private OpenApiRestResourceImportResult resourceImportResult;

    private String httpMethod;

    private FolderEntity folder;

    private List<OpenApiRestRequestImportResult> requestImportResults = new ArrayList<>();

    private Set<String> requestFileNames = new HashSet<>();

    public OpenApiRestMethodImportResult(OpenApiRestResourceImportResult resourceResult, String httpMethod,
            FolderEntity folder) {
        this.resourceImportResult = resourceResult;
        this.httpMethod = httpMethod;
        this.folder = folder;
    }

    public OpenApiRestResourceImportResult getResourceImportResult() {
        return resourceImportResult;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public FolderEntity getFileEntity() {
        return folder;
    }

    public OpenApiRestRequestImportResult[] getRequestImportResults() {
        return requestImportResults.toArray(new OpenApiRestRequestImportResult[requestImportResults.size()]);
    }

    public OpenApiRestRequestImportResult newRequest(String name) {
        requestFileNames.add(name);
        OpenApiRestRequestImportResult requestResult = new OpenApiRestRequestImportResult(this, name);
        getParameters().stream()
                .forEach(p -> requestResult.addParameter(p.getName(), p.getValue(), p.getDescription(), p.getStyle()));
        requestImportResults.add(requestResult);
        return requestResult;
    }

    public boolean isRequestFileNameAvailable(String fileName) {
        return !requestFileNames.contains(fileName);
    }

    @Override
    public OpenApiImportNode getParentImportNode() {
        return resourceImportResult;
    }

    @Override
    public OpenApiImportNode[] getChildImportNodes() {
        return requestImportResults.toArray(new OpenApiImportNode[requestImportResults.size()]);
    }
}