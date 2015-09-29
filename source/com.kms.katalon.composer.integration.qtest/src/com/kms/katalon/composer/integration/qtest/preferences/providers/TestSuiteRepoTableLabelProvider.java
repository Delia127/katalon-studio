package com.kms.katalon.composer.integration.qtest.preferences.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;

public class TestSuiteRepoTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final int CLMN_QTEST_PROJECT_IDX = 0;
    private static final int CLMN_KATALON_FOLDER_IDX = 1;

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (columnIndex < 0 || columnIndex > CLMN_KATALON_FOLDER_IDX) return "";
        if (element == null || !(element instanceof TestSuiteRepo)) return "";

        TestSuiteRepo node = (TestSuiteRepo) element;

        switch (columnIndex) {
            case CLMN_QTEST_PROJECT_IDX:
                return node.getQTestProject().getName();
            case CLMN_KATALON_FOLDER_IDX:
                return node.getFolderId();
        }
        return "";
    }

}
