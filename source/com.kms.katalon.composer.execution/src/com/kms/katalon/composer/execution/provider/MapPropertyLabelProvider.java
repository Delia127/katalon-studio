package com.kms.katalon.composer.execution.provider;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.core.setting.DriverPropertyValueType;

public class MapPropertyLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_NAME_INDEX = 0;
	private static final int COLUMN_TYPE_INDEX = 1;
	private static final int COLUMN_VALUE_INDEX = 2;
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element == null || !(element instanceof Entry)
				|| columnIndex < 0 || columnIndex > COLUMN_VALUE_INDEX) return "";
		final Entry<?, ?> property = (Entry<? , ?>) element;
		switch (columnIndex) {
		case COLUMN_NAME_INDEX:
			return String.valueOf(property.getKey());
		case COLUMN_TYPE_INDEX:
			return DriverPropertyValueType.fromValue(property.getValue()).toString();
		case COLUMN_VALUE_INDEX:
		    return String.valueOf(property.getValue());
		}
		return null;
	}

}
