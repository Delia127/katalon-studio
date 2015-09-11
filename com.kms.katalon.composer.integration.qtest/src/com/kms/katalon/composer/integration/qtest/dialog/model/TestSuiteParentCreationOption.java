package com.kms.katalon.composer.integration.qtest.dialog.model;

public enum TestSuiteParentCreationOption {
    CREATE_ONLY("Create only"),
    CREATE_AND_UPLOAD("Create and upload"),
    CREATE_UPLOAD_AND_SET_AS_DEFAULT("Create, upload and set as default");
    
    private final String text;

    private TestSuiteParentCreationOption(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
