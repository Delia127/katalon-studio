package com.kms.katalon.composer.testcase.ast.variable.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.composer.testcase.parts.VariableTableActionOperator;
import com.kms.katalon.entity.variable.VariableEntity;

public class ChangeVariableMaskedOperation extends AbstractOperation {

    private VariableTableActionOperator testCaseVariablePart;

    private TableViewer tableViewer;

    private VariableEntity variableEntity;

    private boolean oldMaskedValue;

    private boolean newMaskedValue;

    public ChangeVariableMaskedOperation(VariableTableActionOperator testCaseVariablePart, VariableEntity variableEntity,
            boolean newMaskedValue) {
        super(ChangeVariableDescriptionOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableEntity = variableEntity;
        this.newMaskedValue = newMaskedValue;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        oldMaskedValue = variableEntity.isMasked();
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableMasked(newMaskedValue);
        return Status.OK_STATUS;
    }

    private void changeVariableMasked(boolean masked) {
        variableEntity.setMasked(masked);
        testCaseVariablePart.setDirty(true);
        tableViewer.update(variableEntity, null);
        tableViewer.setSelection(new StructuredSelection(variableEntity));
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableMasked(oldMaskedValue);
        return Status.OK_STATUS;
    }
}
