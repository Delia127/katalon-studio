package com.kms.katalon.composer.webui.setting.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.core.webui.setting.DriverProperty;

public class DriverPropertyLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_NAME_INDEX = 0;
	private static final int COLUMN_TYPE_INDEX = 1;
	private static final int COLUMN_VALUE_INDEX = 2;
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null || !(element instanceof DriverProperty)
				|| columnIndex < 0 || columnIndex > COLUMN_VALUE_INDEX) return "";
		DriverProperty property = (DriverProperty) element;
		switch (columnIndex) {
		case COLUMN_NAME_INDEX:
			return property.getName();
		case COLUMN_TYPE_INDEX:
			return property.getValue().getClass().getSimpleName();
		case COLUMN_VALUE_INDEX:
			return String.valueOf(property.getValue());
		}
		return null;
	}

}
