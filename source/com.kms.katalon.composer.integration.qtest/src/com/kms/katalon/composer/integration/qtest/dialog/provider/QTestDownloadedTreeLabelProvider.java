package com.kms.katalon.composer.integration.qtest.dialog.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.composer.integration.qtest.dialog.model.DownloadedPreviewTreeNode;

public class QTestDownloadedTreeLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final int CLM_NAME_INDEX = 0;
    private static final int CLM_TYPE_INDEX = 1;
    private static final int CLM_STATUS_INDEX = 2;

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element != null && element instanceof DownloadedPreviewTreeNode && columnIndex == CLM_NAME_INDEX) {
            DownloadedPreviewTreeNode updatedPreviewTree = (DownloadedPreviewTreeNode) element;
            if ("Test Case".equals(updatedPreviewTree.getType())) {
                return ImageConstants.IMG_16_TEST_CASE;
            } else if ("Module".equals(updatedPreviewTree.getType())) {
                return ImageConstants.IMG_16_FOLDER;
            }
        }
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element != null && element instanceof DownloadedPreviewTreeNode && columnIndex >= CLM_NAME_INDEX
                && columnIndex <= CLM_STATUS_INDEX) {
            DownloadedPreviewTreeNode updatedPreviewTree = (DownloadedPreviewTreeNode) element;
            switch (columnIndex) {
                case CLM_NAME_INDEX:
                    return updatedPreviewTree.getName();
                case CLM_TYPE_INDEX:
                    return updatedPreviewTree.getType();
                case CLM_STATUS_INDEX:
                    return updatedPreviewTree.getStatus();
            }
        }

        return "";
    }

}
