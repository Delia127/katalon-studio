package com.kms.katalon.composer.testcase.ast.variable.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;
import com.kms.katalon.entity.variable.VariableEntity;

public class ChangeVariableDefaultValueOperation extends AbstractOperation {

    private TestCaseVariablePart testCaseVariablePart;

    private TableViewer tableViewer;

    private VariableEntity variableEntity;

    private String oldDefaultValue;

    private String newDefaultValue;

    public ChangeVariableDefaultValueOperation(TestCaseVariablePart testCaseVariablePart, VariableEntity variableEntity,
            String newDefaultValue) {
        super(ChangeVariableDefaultValueOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableEntity = variableEntity;
        this.newDefaultValue = newDefaultValue;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        oldDefaultValue = variableEntity.getDefaultValue();
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableDefaultValue(newDefaultValue);
        return Status.OK_STATUS;
    }

    private void changeVariableDefaultValue(String defaultValue) {
        variableEntity.setDefaultValue(defaultValue);
        testCaseVariablePart.setDirty(true);
        tableViewer.update(variableEntity, null);
        tableViewer.setSelection(new StructuredSelection(variableEntity));
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        changeVariableDefaultValue(oldDefaultValue);
        return Status.OK_STATUS;
    }

}
