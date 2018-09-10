package com.kms.katalon.entity.project;

import com.kms.katalon.entity.file.ClonableObject;

public class SystemFolderConfiguration extends ClonableObject {
    private static final long serialVersionUID = 1437568182750386766L;

    public SystemFolderConfiguration() {
        //Constructor of JAXB initialization
    }
    
    public SystemFolderConfiguration(String url) {
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
