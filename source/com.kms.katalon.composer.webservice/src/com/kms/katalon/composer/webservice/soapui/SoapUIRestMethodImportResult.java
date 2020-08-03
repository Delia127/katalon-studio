package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.folder.FolderEntity;

public class SoapUIRestMethodImportResult extends SoapUIRestResourceImportNode {

    private SoapUIRestResourceImportResult resourceImportResult;

    private String httpMethod;

    private FolderEntity folder;

    private List<SoapUIRestRequestImportResult> requestImportResults = new ArrayList<>();

    private Set<String> requestFileNames = new HashSet<>();

    public SoapUIRestMethodImportResult(SoapUIRestResourceImportResult resourceResult, String httpMethod,
            FolderEntity folder) {
        this.resourceImportResult = resourceResult;
        this.httpMethod = httpMethod;
        this.folder = folder;
    }

    public SoapUIRestResourceImportResult getResourceImportResult() {
        return resourceImportResult;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public FolderEntity getFileEntity() {
        return folder;
    }

    public SoapUIRestRequestImportResult[] getRequestImportResults() {
        return requestImportResults.toArray(new SoapUIRestRequestImportResult[requestImportResults.size()]);
    }

    public SoapUIRestRequestImportResult newRequest(String name) {
        if (!isRequestFileNameAvailable(name)) {
            throw new IllegalArgumentException("Request name already exists.");
        }
        requestFileNames.add(name);

        SoapUIRestRequestImportResult requestResult = new SoapUIRestRequestImportResult(this, name);
        resourceImportResult.getParameters().stream()
                .forEach(p -> requestResult.addParameter(p.getName(), p.getValue(), p.getStyle()));
        getParameters().stream().forEach(p -> requestResult.addParameter(p.getName(), p.getValue(), p.getStyle()));
        requestImportResults.add(requestResult);
        return requestResult;
    }

    public boolean isRequestFileNameAvailable(String fileName) {
        return !requestFileNames.contains(fileName);
    }

    @Override
    public SoapUIImportNode getParentImportNode() {
        return resourceImportResult;
    }

    @Override
    public SoapUIImportNode[] getChildImportNodes() {
        return requestImportResults.toArray(new SoapUIImportNode[requestImportResults.size()]);
    }
}