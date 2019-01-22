package com.kms.katalon.entity.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.IntegratedFileEntity;

public class FolderEntity extends IntegratedFileEntity {
    private static final long serialVersionUID = -3880886097950781395L;

    private FolderType folderType;

    private List<FileEntity> childrenEntities = new ArrayList<FileEntity>();

    public FolderType getFolderType() {
        return this.folderType;
    }

    public void setFolderType(FolderType folderType) {
        this.folderType = folderType;
    }

    public List<FileEntity> getChildrenEntities() {
        return childrenEntities;
    }

    public void setChildrenEntities(List<FileEntity> childrenEntities) {
        this.childrenEntities = childrenEntities;
    }

    @Override
    public String getFileExtension() {
        return "";
    }

    @Override
    public FolderEntity clone() {
        return (FolderEntity) super.clone();
    }

    public enum FolderType {
        TESTCASE("Test case"),
        TESTSUITE("Test suite"),
        DATAFILE("Test data"),
        WEBELEMENT("Object"),
        WEBMELEMENT("Object"),
        WEBWELEMENT("Object"),
        KEYWORD("Keyword"),
        REPORT("Report"),
        CHECKPOINT("Checkpoint"),
        TESTLISTENER("Test listener"),
        PROFILE("Profiles"),
        FEATURE("Feature"),
        INCLUDE("Source");

        private final String text;

        private FolderType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public static String getMetaDataFileExtension() {
        return ".meta";
    }

    public String getMetaDataFileLocation() {
        return getLocation() + File.separator + getMetaDataFileExtension();
    }
}
