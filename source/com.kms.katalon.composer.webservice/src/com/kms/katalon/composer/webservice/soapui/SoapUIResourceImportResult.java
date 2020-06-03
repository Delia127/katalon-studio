package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.List;

public class SoapUIResourceImportResult extends SoapUIResourceElementImportResult {

    private String endpoint;

    private String path;

    private SoapUIImportFileHierarchy folderHierarchy;

    private List<SoapUIMethodImportResult> methodImportResults = new ArrayList<>();

    public SoapUIResourceImportResult(String endpoint, String path, SoapUIImportFileHierarchy folderHierarchy) {
        this.endpoint = endpoint;
        this.path = path;
        this.folderHierarchy = folderHierarchy;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getPath() {
        return path;
    }

    public SoapUIImportFileHierarchy getFolderHierarchy() {
        return folderHierarchy;
    }

    public SoapUIMethodImportResult newMethod(String name, String httpMethod) {
        SoapUIMethodImportResult methodResult = new SoapUIMethodImportResult(this, name, httpMethod);
        methodResult.
        methodImportResults.add(methodResult);
        return methodResult;
    }

    public SoapUIMethodImportResult[] getMethodImportResults() {
        return methodImportResults.toArray(new SoapUIMethodImportResult[methodImportResults.size()]);
    }
}