package com.kms.katalon.composer.testsuite.providers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class FilteredTestCaseLabelProvider extends TypeCheckStyleCellTableLabelProvider<TestCaseEntity> {

    public static final int CLMN_NO_IDX = 0;

    public static final int CLMN_ID_IDX = 1;

    public static final int CLMN_DESCRIPTION_IDX = 2;

    private TableViewer tableViewer;

    public FilteredTestCaseLabelProvider(int columnIndex, TableViewer tableViewer) {
        super(columnIndex);
        this.tableViewer = tableViewer;
    }

    @Override
    protected Class<TestCaseEntity> getElementType() {
        return TestCaseEntity.class;
    }

    @Override
    protected Image getImage(TestCaseEntity testCase) {
        return null;
    }

    @Override
    protected String getText(TestCaseEntity testCase) {
        switch (columnIndex) {
            case CLMN_NO_IDX:
                List<?> input = (List<?>) tableViewer.getInput();
                return Integer.toString(input.indexOf(testCase) + 1);
            case CLMN_ID_IDX:
                return testCase.getIdForDisplay();
            case CLMN_DESCRIPTION_IDX:
                return StringUtils.defaultString(testCase.getDescription());
            default:
                return StringUtils.EMPTY;
        }
    }

}
