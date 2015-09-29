package com.kms.katalon.composer.integration.qtest.dialog.model;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.integration.qtest.entity.QTestModule;

public class ModuleDownloadedPreviewTreeNode implements DownloadedPreviewTreeNode {
    private QTestModule module;
    private boolean isSelected;
    private boolean isAnyChildSelected;
    private List<TestCaseDownloadedPreviewTreeNode> childTestCaseTrees;
    private List<ModuleDownloadedPreviewTreeNode> childModuleTrees;
    private FolderEntity folderEntity;
    private ModuleDownloadedPreviewTreeNode parent;

    public ModuleDownloadedPreviewTreeNode(QTestModule module, FolderEntity folder,
            ModuleDownloadedPreviewTreeNode parent) {
        setModule(module);
        setAnyChildSelected(true);
        setSelected(true);
        this.folderEntity = folder;
        this.parent = parent;
    }

    public boolean isAnyChildSelected() {
        return isAnyChildSelected;
    }

    public void setAnyChildSelected(boolean isAnyChildSelected) {
        this.isAnyChildSelected = isAnyChildSelected;
    }

    public QTestModule getModule() {
        return module;
    }

    public void setModule(QTestModule module) {
        this.module = module;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public List<TestCaseDownloadedPreviewTreeNode> getChildTestCaseTrees() {
        if (childTestCaseTrees == null) childTestCaseTrees = new ArrayList<>();
        return childTestCaseTrees;
    }

    public void setChildTestCaseTrees(List<TestCaseDownloadedPreviewTreeNode> childTestCaseTrees) {
        this.childTestCaseTrees = childTestCaseTrees;
    }

    public List<ModuleDownloadedPreviewTreeNode> getChildModuleTrees() {
        if (childModuleTrees == null) childModuleTrees = new ArrayList<>();
        return childModuleTrees;
    }

    public void setChildModuleTrees(List<ModuleDownloadedPreviewTreeNode> childModuleTrees) {
        this.childModuleTrees = childModuleTrees;
    }

    @Override
    public ModuleDownloadedPreviewTreeNode getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return module.getName();
    }

    @Override
    public String getStatus() {
        if (folderEntity != null) return "Created";

        FolderEntity parentFolderEntity = parent.getFolderEntity();
        if (parentFolderEntity == null) return "New";

        try {
            if (getName().equalsIgnoreCase(
                    FolderController.getInstance().getAvailableFolderName(parentFolderEntity, getName()))) {
                return "New";
            } else {
                return "New but Duplicated";
            }
        } catch (Exception e) {
            return "New";
        }
    }

    @Override
    public String getType() {
        return "Module";
    }

    public FolderEntity getFolderEntity() {
        return folderEntity;
    }

    public void setFolderEntity(FolderEntity folderEntity) {
        this.folderEntity = folderEntity;
    }
}
