package com.kms.katalon.composer.integration.qtest.dialog.model;

import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class TestCaseDownloadedPreviewTreeNode implements DownloadedPreviewTreeNode {
    private QTestTestCase testCase;
    private boolean isSelected;
    private ModuleDownloadedPreviewTreeNode parent;

    public TestCaseDownloadedPreviewTreeNode(ModuleDownloadedPreviewTreeNode parent, QTestTestCase testCase) {
        setTestCase(testCase);
        this.parent = parent;
    }

    public QTestTestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(QTestTestCase testCase) {
        this.testCase = testCase;
    }

    @Override
    public ModuleDownloadedPreviewTreeNode getParent() {
        return parent;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String getName() {
        return testCase.getName();
    }

    @Override
    public String getStatus() {
        try {
            FolderEntity parentFolderEntity = parent.getFolderEntity();
            if (parentFolderEntity == null) {
                return StringConstants.CM_NEW;
            }
            
            if (getName().equalsIgnoreCase(
                    TestCaseController.getInstance().getAvailableTestCaseName(parentFolderEntity, getName()))) {
                return StringConstants.CM_NEW;
            } else {
                return StringConstants.CM_NEW_DUPLICATED;
            }
        } catch (Exception e) {
            return StringConstants.CM_NEW;
        }
    }

    @Override
    public String getType() {
        return StringConstants.TEST_CASE;
    }

}
