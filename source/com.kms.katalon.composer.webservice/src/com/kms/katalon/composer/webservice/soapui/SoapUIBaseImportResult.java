package com.kms.katalon.composer.webservice.soapui;

public abstract class SoapUIBaseImportResult {

    protected SoapUIImportFileHierarchy fileHierarchy;

    protected SoapUIImportFileHierarchy getFileHierarchy() {
        return fileHierarchy;
    }

    protected void setFileHierarchy(SoapUIImportFileHierarchy fileHierarchy) {
        this.fileHierarchy = fileHierarchy;
    }
}
