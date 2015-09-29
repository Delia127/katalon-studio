package com.kms.katalon.composer.integration.qtest.dialog.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;

public class QTestSuiteParentTreeLabelProvider extends LabelProvider implements ITableLabelProvider {
    private static final int CLMN_PARENT_NAME_IDX = 0;
    private static final int CLMN_PARENT_TYPE_IDX = 1;
    private static final int CLMN_PARENT_IS_USED_IDX = 2;

    private List<String> usedParentIds;

    public QTestSuiteParentTreeLabelProvider(List<String> usedParentIds) {
        super();
        this.usedParentIds = usedParentIds;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element != null && element instanceof QTestSuiteParent && columnIndex == CLMN_PARENT_IS_USED_IDX) {
            QTestSuiteParent testSuiteParent = (QTestSuiteParent) element;
            if (usedParentIds.contains(Long.toString(testSuiteParent.getId()))) {
                return ImageConstants.IMG_16_CHECKED;
            } else {
                return ImageConstants.IMG_16_UNCHECKED;
            }
        }
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element == null || !(element instanceof QTestSuiteParent) || columnIndex < 0
                || columnIndex > CLMN_PARENT_IS_USED_IDX) return "";
        QTestSuiteParent testSuiteParent = (QTestSuiteParent) element;
        switch (columnIndex) {
            case CLMN_PARENT_IS_USED_IDX:
                break;
            case CLMN_PARENT_TYPE_IDX:
                return testSuiteParent.getTypeName();
            case CLMN_PARENT_NAME_IDX:
                return testSuiteParent.getName();
        }
        return "";
    }

}
