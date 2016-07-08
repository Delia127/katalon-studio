package com.kms.katalon.composer.checkpoint.parts.supports;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import com.kms.katalon.entity.checkpoint.CheckpointCell;

public class CheckpointCellEditingSupport extends EditingSupport {

    private int columnIndex;

    private MDirtyable dirtyable;

    @Inject
    protected IEventBroker eventBroker;

    public CheckpointCellEditingSupport(ColumnViewer viewer, int columnIndex, MDirtyable dirtyable) {
        super(viewer);
        this.columnIndex = columnIndex;
        this.dirtyable = dirtyable;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new CheckboxCellEditor();
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        CheckpointCell checkpointCell = getCellData(element);
        if (checkpointCell == null) {
            return false;
        }
        return checkpointCell.isChecked();
    }

    @Override
    protected void setValue(Object element, Object value) {
        CheckpointCell checkpointCell = getCellData(element);
        if (checkpointCell == null) {
            return;
        }
        checkpointCell.setChecked((boolean) value);
        getViewer().refresh(element);
        dirtyable.setDirty(true);
    }

    @SuppressWarnings("unchecked")
    private CheckpointCell getCellData(Object element) {
        if (!(element instanceof List) || !(((List<?>) element).get(columnIndex) instanceof CheckpointCell)) {
            return null;
        }
        return ((List<CheckpointCell>) element).get(columnIndex);
    }

}
