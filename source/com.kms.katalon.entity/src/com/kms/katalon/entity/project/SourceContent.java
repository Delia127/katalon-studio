package com.kms.katalon.entity.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.ClonableObject;

public class SourceContent extends ClonableObject implements Serializable {
    private static final long serialVersionUID = 1471837952900668707L;

    private List<SourceFolderConfiguration> sourceFolderList = new ArrayList<>();

    private List<SystemFolderConfiguration> systemFolderList = new ArrayList<>();

    public void setSourceFolderList(List<SourceFolderConfiguration> sourceFolderList) {
        this.sourceFolderList = sourceFolderList;
    }

    public List<SourceFolderConfiguration> getSourceFolderList() {
        return sourceFolderList;
    }

    public void addSourceFolder(SourceFolderConfiguration sourceFolderConfig) {
        sourceFolderList.add(sourceFolderConfig);
    }

    public List<SystemFolderConfiguration> getSystemFolderList() {
        return systemFolderList;
    }

    public void addSystemFolder(SystemFolderConfiguration systemFolderConfig) {
        systemFolderList.add(systemFolderConfig);
    }

}
