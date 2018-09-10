package com.kms.katalon.entity.project;

import com.kms.katalon.entity.file.ClonableObject;

public class SourceFolderConfiguration extends ClonableObject {

    private static final long serialVersionUID = -5002746609456140319L;

    public SourceFolderConfiguration() {
        //Constructor of JAXB initialization
    }

    public SourceFolderConfiguration(String url) {
        this.url = url;
    }

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
