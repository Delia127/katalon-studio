package com.kms.katalon.composer.testcase.ast.variable.operations;

import java.util.List;

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

public class NewVariableOperation extends AbstractOperation {
    private VariableEntity newVariable;
    private TableActionOperator testCaseVariablePart;
    private TableViewer tableViewer;
    private List<VariableEntity> variableList;
    
    public NewVariableOperation(TableActionOperator testCaseVariablePart, VariableEntity newVariable) {
        super(NewVariableOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableList = testCaseVariablePart.getVariablesList();
        this.newVariable = newVariable;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (newVariable == null) {
            return Status.CANCEL_STATUS;
        }
        
        variableList.add(newVariable);        
        tableViewer.refresh();
        tableViewer.setSelection(new StructuredSelection(newVariable));
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (newVariable == null) {
            return Status.CANCEL_STATUS;
        }
        variableList.remove(newVariable);
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

}
