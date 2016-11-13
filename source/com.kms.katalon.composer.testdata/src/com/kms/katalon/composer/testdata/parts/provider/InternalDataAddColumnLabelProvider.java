package com.kms.katalon.composer.testdata.parts.provider;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.providers.ImageCenterLabelProvider;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.parts.InternalDataRow;

public class InternalDataAddColumnLabelProvider extends ImageCenterLabelProvider<InternalDataRow> {

    private static final int FIRST_COLUMN_IDX = 0;

    public InternalDataAddColumnLabelProvider() {
        super(FIRST_COLUMN_IDX);
    }

    @Override
    protected Class<InternalDataRow> getElementType() {
        return InternalDataRow.class;
    }

    @Override
    protected Image getImage(InternalDataRow element) {
        if (element.isLastRow()) {
            return ImageConstants.IMG_16_ADD;
        }
        return null;
    }

    @Override
    protected String getText(InternalDataRow element) {
        if (element.isLastRow()) {
            return StringUtils.EMPTY;
        }

        List<?> input = (List<?>) getViewer().getInput();
        return Integer.toString(input.indexOf(element) + 1);
    }

    @Override
    protected String getElementToolTipText(InternalDataRow element) {
        if (element.isLastRow()) {
            return StringConstants.PA_TOOL_TIP_ADD_ROW;
        }
        return StringUtils.defaultIfEmpty(getText(element), null);
    }
}
