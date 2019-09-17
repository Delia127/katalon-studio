package com.kms.katalon.composer.windows.spy;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;

public class SelectableWindowsElementEditingSupport extends TypeCheckedEditingSupport<CapturedWindowsElement> {

    public SelectableWindowsElementEditingSupport(ColumnViewer viewer) {
        super(viewer);
    }

    @Override
    protected Class<CapturedWindowsElement> getElementType() {
        return CapturedWindowsElement.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(CapturedWindowsElement element) {
        return new CheckboxCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(CapturedWindowsElement element) {
        return true;
    }

    @Override
    protected Object getElementValue(CapturedWindowsElement element) {
        return element.isChecked();
    }

    @Override
    protected void setElementValue(CapturedWindowsElement element, Object value) {
        if (!(value instanceof Boolean)) {
            return;
        }
        boolean newSelection = (boolean) value;
        if (element.isChecked() != newSelection) {
            element.setChecked(newSelection);
            getViewer().refresh(element);
            getViewer().notifyStateChanged();
        }
    }

    @Override
    public CapturedWindowsObjectTableViewer getViewer() {
        return (CapturedWindowsObjectTableViewer) super.getViewer();
    }
}
