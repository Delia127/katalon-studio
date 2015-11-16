package com.kms.katalon.composer.execution.components;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.kms.katalon.core.setting.DriverPropertyValueType;

public class DriverPropertyValueEditingSupport extends EditingSupport {
    private TableViewer tableViewer;

    public DriverPropertyValueEditingSupport(TableViewer tableViewer) {
        super(tableViewer);
        this.tableViewer = tableViewer;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        DriverPropertyValueType propertyType = DriverPropertyValueType.fromValue(((Entry<?, ?>) element).getValue());
        switch (propertyType) {
        case Boolean:
            return new ComboBoxCellEditor(tableViewer.getTable(), new String[] { Boolean.TRUE.toString().toLowerCase(),
                    Boolean.FALSE.toString().toLowerCase() });
        case List:
            return new ListPropertyValueCellEditor(tableViewer.getTable());
        case Dictionary:
            return new MapPropertyValueCellEditor(tableViewer.getTable());
        case Integer:
        case String:
            return new TextCellEditor(tableViewer.getTable());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return element instanceof Entry;
    }

    @Override
    protected Object getValue(Object element) {
        DriverPropertyValueType propertyType = DriverPropertyValueType.fromValue(((Entry<?, ?>) element).getValue());
        switch (propertyType) {
        case Boolean:
            Boolean booleanValue = (Boolean) ((Entry<?, ?>) element).getValue();
            return booleanValue == true ? 0 : 1;
        case List:
        case Dictionary:
            return ((Entry<?, ?>) element).getValue();
        case Integer:
        case String:
            return String.valueOf(((Entry<?, ?>) element).getValue());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setValue(Object element, Object value) {
        Entry<?, Object> property = ((Entry<?, Object>) element);
        DriverPropertyValueType propertyType = DriverPropertyValueType.fromValue(property.getValue());
        switch (propertyType) {
        case Boolean:
            if (value instanceof Integer) {
                Integer integerValue = (Integer) value;
                if (integerValue == 0) {
                    property.setValue(Boolean.TRUE);
                } else if (integerValue == 1) {
                    property.setValue(Boolean.FALSE);
                }
            }
            break;
        case Integer:
            try {
                property.setValue(Integer.valueOf(String.valueOf(value)));
            } catch (NumberFormatException e) {
                // not a number, so not setting value
            }
            break;
        case List:
        case Dictionary:
            DriverPropertyValueType newPropertyType = DriverPropertyValueType.fromValue(value);
            if (newPropertyType == propertyType) {
                property.setValue(value);
            }
            break;
        case String:
            if (value instanceof String) {
                property.setValue(value);
            }
            break;
        }
        tableViewer.refresh();
    }

}
