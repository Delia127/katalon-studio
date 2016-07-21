package com.kms.katalon.composer.components.impl.providers;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.impl.control.CTableViewer;

public abstract class TypeCheckStyleCellTableLabelProvider <T> extends TypeCheckedStyleCellLabelProvider<T> {

    public TypeCheckStyleCellTableLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    public void initialize(ColumnViewer viewer, ViewerColumn column) {
        super.initialize(viewer, column);
    }

    protected ViewerCell getOwnedViewerCell(Event event) {
        CTableViewer tableViewer = (CTableViewer) getViewer();
        return tableViewer.getViewerRowFromItem(event.item).getCell(columnIndex);
    }
    
    @Override
    protected boolean canNotDrawSafely(Object element) {
        return super.canNotDrawSafely(element) || !(getViewer() instanceof CTableViewer);
    }
}
