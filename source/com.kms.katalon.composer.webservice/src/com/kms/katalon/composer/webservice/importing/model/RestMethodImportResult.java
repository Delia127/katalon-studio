package com.kms.katalon.composer.webservice.importing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class RestMethodImportResult extends RestResourceImportNode {

    private RestResourceImportResult resourceImportResult;

    private String httpMethod;

    private FolderEntity folder;

    private List<RestRequestImportResult> requestImportResults = new ArrayList<>();

    private Set<String> requestFileNames = new HashSet<>();

    public RestMethodImportResult(RestResourceImportResult resourceResult, String httpMethod, FolderEntity folder) {
        this.resourceImportResult = resourceResult;
        this.httpMethod = httpMethod;
        this.folder = folder;
    }

    public RestResourceImportResult getResourceImportResult() {
        return resourceImportResult;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public FileEntity getFileEntity() {
        return folder;
    }

    public List<RestRequestImportResult> getRequestImportResults() {
        return Collections.unmodifiableList(requestImportResults);
    }

    public <T extends RestRequestImportResult> T newRequest(String name, Supplier<T> requestSupplier) {
        if (!isRequestFileNameAvailable(name)) {
            throw new IllegalArgumentException("Request name already exists.");
        }
        requestFileNames.add(name);

        T requestResult = requestSupplier.get();
        requestResult.setName(name);
        resourceImportResult.getParameters().stream().forEach(p -> {
            RestParameterImportResult pr = requestResult.addNewParameter(p.getName());
            pr.setValue(p.getValue());
            pr.setStyle(p.getStyle());
        });
        getParameters().stream().forEach(p -> {
            String nm = p.getName();
            RestParameterImportResult pr = requestResult.hasParameter(nm) ? requestResult.getParameter(nm)
                    : requestResult.addNewParameter(nm);
            pr.setValue(p.getValue());
            pr.setStyle(p.getStyle());
        });
        requestImportResults.add(requestResult);
        return requestResult;
    }

    public boolean isRequestFileNameAvailable(String fileName) {
        return !requestFileNames.contains(fileName);
    }

    public RestImportNode getParentImportNode() {
        return resourceImportResult;
    }

    public List<RestImportNode> getChildImportNodes() {
        return Collections.unmodifiableList(requestImportResults);
    }
}