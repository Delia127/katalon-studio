package com.kms.katalon.composer.testcase.ast.variable.operations;

import java.util.ArrayList;
import java.util.List;

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

public class ClearVariableOperation extends AbstractOperation {
    private VariableTableActionOperator testCaseVariablePart;

    private TableViewer tableViewer;

    private List<VariableEntity> variableList;

    private List<VariableEntity> deletedVariableList;

    public ClearVariableOperation(VariableTableActionOperator testCaseVariablePart) {
        super(ClearVariableOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableList = testCaseVariablePart.getVariablesList();
        this.deletedVariableList = new ArrayList<VariableEntity>();
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        deletedVariableList.clear();
        deletedVariableList.addAll(variableList);
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        variableList.clear();
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        variableList.addAll(deletedVariableList);
        tableViewer.refresh();
        tableViewer.setSelection(new StructuredSelection(deletedVariableList.toArray()));
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

}
