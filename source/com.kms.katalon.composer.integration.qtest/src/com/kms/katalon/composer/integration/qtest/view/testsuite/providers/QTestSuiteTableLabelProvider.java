package com.kms.katalon.composer.integration.qtest.view.testsuite.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.integration.qtest.entity.QTestCycle;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;

public class QTestSuiteTableLabelProvider extends LabelProvider implements ITableLabelProvider {
    private static final int CLMN_PARENT_NAME_IDX = 0;
    private static final int CLMN_PARENT_TYPE_IDX = 1;
    private static final int CLMN_PARENT_IS_DEFAULT_IDX = 2;
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element != null && element instanceof QTestSuite) {
            QTestSuite qTestSuite = (QTestSuite) element;
            switch (columnIndex) {
                case CLMN_PARENT_NAME_IDX:
                    if (qTestSuite.getId() > 0) {
                        return ImageConstants.IMG_16_UPLOADED;
                    } else {
                        return ImageConstants.IMG_16_UPLOADING;
                    }
                case CLMN_PARENT_IS_DEFAULT_IDX:
                    if (qTestSuite.isSelected()) {
                        return ImageConstants.IMG_16_CHECKED;
                    } else {
                        return ImageConstants.IMG_16_UNCHECKED;
                    }
            }

        }
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element == null || !(element instanceof QTestSuite) || columnIndex < CLMN_PARENT_NAME_IDX
                || columnIndex > CLMN_PARENT_TYPE_IDX) return "";

        QTestSuite qTestSuite = (QTestSuite) element;
        switch (columnIndex) {
            case CLMN_PARENT_NAME_IDX:
                QTestSuiteParent parent = qTestSuite.getParent();
                if (parent instanceof QTestCycle && parent.getParent() != null) {
                    String parentName = parent.getParent().getName();
                    if (parent != null && !parentName.isEmpty()) {
                        return parent.getParent().getName() + " / " + parent.getName();
                    } else {
                        return parent.getName();
                    }

                } else {
                    return parent.getName();
                }

            case CLMN_PARENT_TYPE_IDX:
                return qTestSuite.getParent().getTypeName();
        }
        return "";
    }

}
