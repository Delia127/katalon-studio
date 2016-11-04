package com.kms.katalon.composer.checkpoint.parts.supports;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import com.kms.katalon.composer.checkpoint.parts.CheckpointAbstractPart;
import com.kms.katalon.entity.checkpoint.CheckpointCell;

public class CheckpointCellEditingSupport extends EditingSupport {

    private int columnIndex;

    private CheckpointAbstractPart parentPart;

    @Inject
    protected IEventBroker eventBroker;

    public CheckpointCellEditingSupport(ColumnViewer viewer, int columnIndex, CheckpointAbstractPart parentPart) {
        super(viewer);
        this.columnIndex = columnIndex;
        this.parentPart = parentPart;
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
        parentPart.executeOperation(new EditCheckpointCellOperation(element, (boolean) value));
    }

    private CheckpointCell getCellData(Object element) {
        if (!(element instanceof List) || !(((List<?>) element).get(columnIndex) instanceof CheckpointCell)) {
            return null;
        }
        return (CheckpointCell) ((List<?>) element).get(columnIndex);
    }

    private class EditCheckpointCellOperation extends AbstractOperation {
        private boolean isChecked;

        private CheckpointCell checkpointCell;

        private Object element;

        private ColumnViewer viewer;

        public EditCheckpointCellOperation(Object element, boolean isChecked) {
            super(EditCheckpointCellOperation.class.getName());
            this.element = element;
            this.isChecked = isChecked;
            viewer = getViewer();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            this.checkpointCell = getCellData(element);
            if (checkpointCell == null) {
                return Status.CANCEL_STATUS;
            }
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doCheck(isChecked);
            return Status.OK_STATUS;
        }

        protected void doCheck(boolean isChecked) {
            checkpointCell.setChecked(isChecked);
            viewer.refresh(element);
            parentPart.setDirty(true);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doCheck(!isChecked);
            return Status.OK_STATUS;
        }
    }
}
