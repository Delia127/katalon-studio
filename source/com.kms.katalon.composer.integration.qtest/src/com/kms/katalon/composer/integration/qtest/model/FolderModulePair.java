package com.kms.katalon.composer.integration.qtest.model;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.integration.qtest.entity.QTestModule;

public class FolderModulePair {
    private FolderEntity folder;

    private QTestModule module;

    public FolderModulePair(FolderEntity left, QTestModule right) {
        folder = left;
        module = right;
    }

    public FolderEntity getFolder() {
        return folder;
    }

    public QTestModule getModule() {
        return module;
    }

    public void setModule(QTestModule module) {
        this.module = module;
    }
}
