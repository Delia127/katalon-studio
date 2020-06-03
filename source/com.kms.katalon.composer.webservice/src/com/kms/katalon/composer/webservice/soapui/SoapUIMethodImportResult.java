package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.List;

public class SoapUIMethodImportResult extends SoapUIResourceElementImportResult {

    private SoapUIResourceImportResult resourceImportResult;

    private String name;

    private String httpMethod;
    
    private List<SoapUIRequestImportResult> requestImportResults = new ArrayList<>();

    public SoapUIMethodImportResult(SoapUIResourceImportResult resourceHolder, String name, String httpMethod) {
        this.resourceImportResult = resourceHolder;
        this.name = name;
        this.httpMethod = httpMethod;
    }

    public SoapUIResourceImportResult getResourceImportResult() {
        return resourceImportResult;
    }

    public String getName() {
        return name;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
    
    public SoapUIRequestImportResult[] getRequestImportResults() {
        return requestImportResults.toArray(new SoapUIRequestImportResult[requestImportResults.size()]);
    }
}