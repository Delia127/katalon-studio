package com.kms.katalon.composer.integration.qtest.dialog.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.integration.qtest.entity.QTestModule;

public class TestCaseRootSelectionTreeLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (element != null && element instanceof QTestModule) {
            return ImageConstants.IMG_16_FOLDER;
        }
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element != null && element instanceof QTestModule) {
            QTestModule qTestModule = (QTestModule) element;
            return qTestModule.getName();
        }
        return "";
    }

}
