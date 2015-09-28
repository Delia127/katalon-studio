package com.kms.katalon.composer.execution.components;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.core.setting.DriverPropertyValueType;

public class DriverPropertyTypeEditingSupport extends EditingSupport {
    private TableViewer tableViewer;

    public DriverPropertyTypeEditingSupport(TableViewer tableViewer) {
        super(tableViewer);
        this.tableViewer = tableViewer;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor(tableViewer.getTable(), DriverPropertyValueType.stringValues());
    }

    @Override
    protected boolean canEdit(Object element) {
        return element instanceof Entry;
    }

    @Override
    protected Object getValue(Object element) {
        DriverPropertyValueType valueType = DriverPropertyValueType.fromValue(((Entry<?, ?>) element).getValue());
        for (int i = 0; i < DriverPropertyValueType.values().length; i++) {
            if (DriverPropertyValueType.values()[i] == valueType) {
                return i;
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setValue(Object element, Object value) {
        if (value instanceof Integer) {
            DriverPropertyValueType valueType = DriverPropertyValueType.fromValue(((Entry<?, ?>) element).getValue());
            DriverPropertyValueType newType = DriverPropertyValueType
                    .valueOf(DriverPropertyValueType.stringValues()[(Integer) value]);
            if (valueType != newType) {
                ((Entry<?, Object>) element).setValue(newType.getDefaultValue());
                tableViewer.refresh();
            }
        }
    }

}
