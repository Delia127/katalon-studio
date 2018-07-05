package com.kms.katalon.composer.testcase.ast.variable.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.composer.testcase.parts.TableActionOperator;
import com.kms.katalon.entity.variable.VariableEntity;

public class ChangeVariableDescriptionOperation extends AbstractOperation {

    private TableActionOperator testCaseVariablePart;

    private TableViewer tableViewer;

    private VariableEntity variableEntity;

    private String oldDescription;

    private String newDescription;

    public ChangeVariableDescriptionOperation(TableActionOperator testCaseVariablePart, VariableEntity variableEntity,
            String newDescription) {
        super(ChangeVariableDescriptionOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableEntity = variableEntity;
        this.newDescription = newDescription;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        oldDescription = variableEntity.getDescription();
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableDescription(newDescription);
        return Status.OK_STATUS;
    }

    private void changeVariableDescription(String description) {
        variableEntity.setDescription(description);
        testCaseVariablePart.setDirty(true);
        tableViewer.update(variableEntity, null);
        tableViewer.setSelection(new StructuredSelection(variableEntity));
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableDescription(oldDescription);
        return Status.OK_STATUS;
    }

}
