package com.kms.katalon.composer.components.impl.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerCell;

public abstract class TypeCheckedEditingSupport<T> extends EditingSupport {

    protected int columnIndex;

    public TypeCheckedEditingSupport(ColumnViewer viewer) {
        super(viewer);
    }

    protected abstract Class<T> getElementType();

    private boolean isElementInstanceOf(Object element) {
        Class<?> clazz = getElementType();
        return clazz != null && clazz.isInstance(element);
    }

    @SuppressWarnings("unchecked")
    protected CellEditor getCellEditor(Object element) {
        if (isElementInstanceOf(element)) {
            return getCellEditorByElement((T) element);
        }
        return defaultCellEditorIfNotInstanceOf();
    }

    protected CellEditor defaultCellEditorIfNotInstanceOf() {
        return null;
    }

    protected abstract CellEditor getCellEditorByElement(T element);

    @SuppressWarnings("unchecked")
    @Override
    protected boolean canEdit(Object element) {
        if (isElementInstanceOf(element)) {
            return canEditElement((T) element);
        }
        return defaultCanEditValueIfNotInstanceOf();
    }

    protected boolean defaultCanEditValueIfNotInstanceOf() {
        return false;
    }

    protected abstract boolean canEditElement(T element);

    @SuppressWarnings("unchecked")
    protected Object getValue(Object element) {
        if (isElementInstanceOf(element)) {
            return getElementValue((T) element);
        }
        return defaultDisplayValueIfNotInstanceOf();
    }

    protected abstract Object getElementValue(T element);

    protected Object defaultDisplayValueIfNotInstanceOf() {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void setValue(Object element, Object value) {
        if (isElementInstanceOf(element)) {
            setElementValue((T) element, value);
        }
    }

    protected abstract void setElementValue(T element, Object value);

    @Override
    protected void initializeCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
        columnIndex = cell.getColumnIndex();
        super.initializeCellEditorValue(cellEditor, cell);
    }

    @Override
    protected void saveCellEditorValue(CellEditor cellEditor, ViewerCell cell) {
        columnIndex = cell.getColumnIndex();
        super.saveCellEditorValue(cellEditor, cell);
    }
}
