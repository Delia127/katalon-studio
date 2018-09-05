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

public class ChangeVariableNameOperation extends AbstractOperation {

    private TableActionOperator testCaseVariablePart;

    private TableViewer tableViewer;

    private VariableEntity variableEntity;

    private String oldVariableName;

    private String newVariableName;

    public ChangeVariableNameOperation(TableActionOperator testCaseVariablePart, VariableEntity variableEntity,
            String newVariableName) {
        super(ChangeVariableNameOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableEntity = variableEntity;
        this.newVariableName = newVariableName;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        oldVariableName = variableEntity.getName();
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableName(newVariableName);
        return Status.OK_STATUS;
    }

    private void changeVariableName(String variableName) {
        variableEntity.setName(variableName);
        testCaseVariablePart.setDirty(true);
        tableViewer.update(variableEntity, null);
        tableViewer.setSelection(new StructuredSelection(variableEntity));
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableName(oldVariableName);
        return Status.OK_STATUS;
    }

}
