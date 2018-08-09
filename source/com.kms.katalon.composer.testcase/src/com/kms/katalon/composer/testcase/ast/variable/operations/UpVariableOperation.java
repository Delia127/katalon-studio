package com.kms.katalon.composer.testcase.ast.variable.operations;

import java.util.Collections;
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

public class UpVariableOperation extends AbstractOperation {
    private VariableTableActionOperator testCaseVariablePart;

    private TableViewer tableViewer;

    protected List<VariableEntity> variableList;
    
    private int moveIndex = -1;

    public UpVariableOperation(VariableTableActionOperator testCaseVariablePart) {
        this(UpVariableOperation.class.getName(), testCaseVariablePart);
    }
    
    public UpVariableOperation(String label, VariableTableActionOperator testCaseVariablePart) {
        super(label);
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableList = testCaseVariablePart.getVariablesList();
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty()) {
            return Status.CANCEL_STATUS;
        }
        VariableEntity variable = (VariableEntity) selection.getFirstElement();
        int index = variableList.indexOf(variable);
        if (isOutOfBound(index)) {
            return Status.CANCEL_STATUS;
        }
        moveIndex = index;
        return redo(monitor, info);
    }

    protected boolean isOutOfBound(int index) {
        return index <= 0;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        Collections.swap(variableList, moveIndex, moveIndex + getOffset());
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

    protected int getOffset() {
        return -1;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        Collections.swap(variableList, moveIndex + getOffset(), moveIndex);
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

}
