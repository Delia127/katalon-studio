package com.kms.katalon.composer.execution.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.core.setting.DriverPropertyValueType;

public class ListPropertyLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final int COLUMN_TYPE_INDEX = 0;
    private static final int COLUMN_VALUE_INDEX = 1;

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element == null || columnIndex < 0 || columnIndex > COLUMN_VALUE_INDEX) {
            return "";
        }
        switch (columnIndex) {
        case COLUMN_TYPE_INDEX:
            return DriverPropertyValueType.fromValue(element).toString();
        case COLUMN_VALUE_INDEX:
            return String.valueOf(element);
        }
        return null;
    }

}
