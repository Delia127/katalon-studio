package com.kms.katalon.composer.integration.qtest.dialog.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.integration.qtest.entity.QTestLog;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;

public class TestCaseResultPreviewUploadedLableProvider extends LabelProvider implements ITableLabelProvider {

    private static final int CLMN_NO_IDX = 0;
    private static final int CLMN_NAME_IDX = 1;
    private static final int CLMN_ATTACHMENT_IDX = 2;
    private static final int CLMN_MESSAGE_IDX = 3;

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element == null || !(element instanceof QTestLogUploadedPreview)) return null;
        if ((columnIndex < CLMN_NO_IDX) || (columnIndex > CLMN_MESSAGE_IDX)) return null;

        if (columnIndex == CLMN_ATTACHMENT_IDX) {
            QTestLogUploadedPreview uploadedResult = (QTestLogUploadedPreview) element;
            QTestLog testCaseResult = uploadedResult.getQTestLog();
            return (testCaseResult.isAttachmentIncluded()) ? ImageConstants.IMG_16_CHECKED
                    : ImageConstants.IMG_16_UNCHECKED;
        }

        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element == null || !(element instanceof QTestLogUploadedPreview)) return "";
        if ((columnIndex < CLMN_NO_IDX) || (columnIndex > CLMN_MESSAGE_IDX)) return "";

        QTestLogUploadedPreview uploadedResult = (QTestLogUploadedPreview) element;
        QTestLog testCaseResult = uploadedResult.getQTestLog();
        switch (columnIndex) {
            case CLMN_NO_IDX: {
                return Integer.toString(uploadedResult.getTestLogIndex() + 1);
            }
            case CLMN_NAME_IDX: {
                return testCaseResult.getName();
            }
            case CLMN_ATTACHMENT_IDX: {
                return ""; // this column uses image only.
            }
            case CLMN_MESSAGE_IDX: {
                return testCaseResult.getMessage();
            }
        }
        return "";
    }

}
