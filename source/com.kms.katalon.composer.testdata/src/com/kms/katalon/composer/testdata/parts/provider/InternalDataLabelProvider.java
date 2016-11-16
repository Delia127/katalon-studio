package com.kms.katalon.composer.testdata.parts.provider;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.parts.InternalDataRow;
import com.kms.katalon.composer.testdata.parts.InternalTestDataPart;
import com.kms.katalon.core.testdata.TestData;

public class InternalDataLabelProvider extends TypeCheckedStyleCellLabelProvider<InternalDataRow> {

    public static final int FIRST_COLUMN_IDX = 0;

    public InternalDataLabelProvider() {
        super(FIRST_COLUMN_IDX);
    }

    @Override
    protected Class<InternalDataRow> getElementType() {
        return InternalDataRow.class;
    }

    @Override
    protected Image getImage(InternalDataRow element) {
        return null;
    }

    @Override
    protected String getText(InternalDataRow element) {
        if (element.isLastRow()) {
            return StringUtils.EMPTY;
        }

        if (columnIndex == FIRST_COLUMN_IDX) {
            List<?> input = (List<?>) getViewer().getInput();
            return Integer.toString(input.indexOf(element) + TestData.BASE_INDEX);
        }

        return element.getCells().get(columnIndex - InternalTestDataPart.BASE_COLUMN_INDEX).getValue();
    }

    @Override
    public void update(ViewerCell cell) {
        columnIndex = cell.getColumnIndex();
        super.update(cell);
    }
    
    @Override
    protected String getElementToolTipText(InternalDataRow element) {
        if (element.isLastRow()) {
            return StringConstants.PA_TOOL_TIP_ADD_ROW;
        }
        return StringUtils.defaultIfEmpty(getText(element), null);
    }
}
