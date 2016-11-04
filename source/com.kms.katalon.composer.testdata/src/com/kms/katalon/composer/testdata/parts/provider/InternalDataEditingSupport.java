package com.kms.katalon.composer.testdata.parts.provider;

import static com.kms.katalon.composer.testdata.parts.InternalTestDataPart.BASE_COLUMN_INDEX;

import java.util.List;
import java.util.Objects;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testdata.parts.InternalDataCell;
import com.kms.katalon.composer.testdata.parts.InternalDataRow;
import com.kms.katalon.composer.testdata.parts.InternalTestDataPart;

public class InternalDataEditingSupport extends TypeCheckedEditingSupport<InternalDataRow> {
    private InternalTestDataPart testDataPart;

    public InternalDataEditingSupport(InternalTestDataPart testDataPart, ColumnViewer viewer) {
        super(viewer);
        this.testDataPart = testDataPart;
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
        if (!(value instanceof String)) {
            return;
        }
        testDataPart.executeOperation(new EditCellOperation(element, (String) value));
    }

    private InternalDataCell getCellData(InternalDataRow element) {
        return element.getCells().get(columnIndex - BASE_COLUMN_INDEX);
    }

    private class EditCellOperation extends AbstractOperation {
        private int rowIndex;

        private String newValue;

        private String oldValue;

        public EditCellOperation(InternalDataRow rowElement, String newValue) {
            super(EditCellOperation.class.getName());
            this.rowIndex = testDataPart.getInput().indexOf(rowElement);
            this.newValue = newValue;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            List<InternalDataRow> input = testDataPart.getInput();
            if (rowIndex < 0 || rowIndex > input.size()) {
                return Status.CANCEL_STATUS;
            }
            InternalDataCell internalDataCell = getCellData(input.get(rowIndex));
            if (newValue.equals(internalDataCell.getValue())) {
                return Status.CANCEL_STATUS;
            }
            oldValue = internalDataCell.getValue();
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doSetValue(newValue);
            return Status.OK_STATUS;
        }

        private void doSetValue(String value) {
            List<InternalDataRow> input = testDataPart.getInput();
            InternalDataRow rowElement = input.get(rowIndex);
            InternalDataCell internalDataCell = getCellData(rowElement);
            internalDataCell.setValue(Objects.toString(value));
            getViewer().refresh(rowElement);
            getViewer().setSelection(new StructuredSelection(rowElement));
            testDataPart.setDirty(true);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doSetValue(oldValue);
            return Status.OK_STATUS;
        }

    }
}
