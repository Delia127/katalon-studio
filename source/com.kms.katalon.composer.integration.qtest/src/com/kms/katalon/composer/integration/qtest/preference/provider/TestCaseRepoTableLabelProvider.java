package com.kms.katalon.composer.integration.qtest.preference.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.integration.qtest.entity.QTestModule;

public class TestCaseRepoTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final int CLMN_QTEST_PROJECT_IDX = 0;
    private static final int CLMN_QTEST_MODULE_IDX = 1;
    private static final int CLMN_KATALON_FOLDER_IDX = 2;

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (columnIndex < 0 || columnIndex > CLMN_KATALON_FOLDER_IDX) return "";
        if (element == null || !(element instanceof TestCaseRepo)) return "";

        TestCaseRepo node = (TestCaseRepo) element;

        switch (columnIndex) {
            case CLMN_QTEST_PROJECT_IDX:
                return node.getQTestProject().getName();
            case CLMN_QTEST_MODULE_IDX:
                QTestModule module = node.getQTestModule();
                if (module != null && module.getName() != null) {
                    return node.getQTestModule().getName();
                } else {
                    return "";
                }
            case CLMN_KATALON_FOLDER_IDX:
                return node.getFolderId();
        }
        return "";
    }

}
