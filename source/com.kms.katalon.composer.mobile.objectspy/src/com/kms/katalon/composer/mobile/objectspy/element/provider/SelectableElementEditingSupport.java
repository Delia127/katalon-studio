package com.kms.katalon.composer.mobile.objectspy.element.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.viewer.CapturedObjectTableViewer;

public class SelectableElementEditingSupport extends TypeCheckedEditingSupport<CapturedMobileElement> {

    public SelectableElementEditingSupport(ColumnViewer viewer) {
        super(viewer);
    }

    @Override
    protected Class<CapturedMobileElement> getElementType() {
        return CapturedMobileElement.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(CapturedMobileElement element) {
        return new CheckboxCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(CapturedMobileElement element) {
        return true;
    }

    @Override
    protected Object getElementValue(CapturedMobileElement element) {
        return element.isChecked();
    }

    @Override
    protected void setElementValue(CapturedMobileElement element, Object value) {
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
    public CapturedObjectTableViewer getViewer() {
        return (CapturedObjectTableViewer) super.getViewer();
    }
}
