package com.kms.katalon.composer.integration.qtest.dialog.model;

public interface DownloadedPreviewTreeNode {
    boolean isSelected();

    void setSelected(boolean isSelected);

    ModuleDownloadedPreviewTreeNode getParent();

    String getName();

    String getStatus();

    String getType();
}
