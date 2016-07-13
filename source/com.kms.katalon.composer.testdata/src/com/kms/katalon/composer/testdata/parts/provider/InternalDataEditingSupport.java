package com.kms.katalon.composer.testdata.parts.provider;

import static com.kms.katalon.composer.testdata.parts.InternalTestDataPart.BASE_COLUMN_INDEX;

import java.util.Objects;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testdata.parts.InternalDataCell;
import com.kms.katalon.composer.testdata.parts.InternalDataRow;

public class InternalDataEditingSupport extends TypeCheckedEditingSupport<InternalDataRow> {

    private MDirtyable dirty;

    public InternalDataEditingSupport(ColumnViewer viewer, MDirtyable dirty) {
        super(viewer);
        this.dirty = dirty;
    }

    @Override
    protected Class<InternalDataRow> getElementType() {
        return InternalDataRow.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(InternalDataRow element) {
        return new TextCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected boolean canEditElement(InternalDataRow element) {
        return !element.isLastRow();
    }

    @Override
    protected Object getElementValue(InternalDataRow element) {
        return getCellData(element).getValue();
    }

    @Override
    protected void setElementValue(InternalDataRow element, Object value) {
        InternalDataCell internalDataCell = getCellData(element);
        if (value instanceof String && !value.equals(internalDataCell.getValue())) {
            internalDataCell.setValue(Objects.toString(value));
            getViewer().refresh(element);
            dirty.setDirty(true);
        }
    }

    private InternalDataCell getCellData(InternalDataRow element) {
        return element.getCells().get(columnIndex - BASE_COLUMN_INDEX);
    }

}
