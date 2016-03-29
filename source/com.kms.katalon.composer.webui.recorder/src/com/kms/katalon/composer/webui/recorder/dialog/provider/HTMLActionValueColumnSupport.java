package com.kms.katalon.composer.webui.recorder.dialog.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;

public class HTMLActionValueColumnSupport extends EditingSupport {
    public HTMLActionValueColumnSupport(ColumnViewer viewer) {
        super(viewer);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        HTMLActionParamValueType inputValueType = getParamValue(element);
        return inputValueType.getEditorProvider().getCellEditorForValue((Composite) getViewer().getControl(),
                inputValueType.getValue());
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof HTMLActionParamMapping);
    }

    private HTMLActionParamValueType getParamValue(Object element) {
        return ((HTMLActionParamMapping) element).getActionData();
    }

    @Override
    protected Object getValue(Object element) {
        HTMLActionParamValueType inputValueType = getParamValue(element);
        return (inputValueType != null) ? inputValueType.getValueToEdit() : null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (value == null) {
            return;
        }
        HTMLActionParamValueType paramValueType = getParamValue(element);
        paramValueType.setValue(value);

        getViewer().refresh(element);
    }
}
