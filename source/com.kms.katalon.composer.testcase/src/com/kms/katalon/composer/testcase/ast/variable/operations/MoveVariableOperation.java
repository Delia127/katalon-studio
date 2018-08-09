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

import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;
import com.kms.katalon.composer.testcase.parts.VariableTableActionOperator;
import com.kms.katalon.entity.variable.VariableEntity;

public class MoveVariableOperation extends AbstractOperation {
    private TestCaseVariablePart testCaseVariablePart;

    private TableViewer tableViewer;

    private List<VariableEntity> variableList;

    private int currentIndex;

    private int newIndex;

    public MoveVariableOperation(VariableTableActionOperator testCaseVariablePart, int currentIndex, int newIndex) {
        super(MoveVariableOperation.class.getName());
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableList = testCaseVariablePart.getVariablesList();
        this.newIndex = newIndex;
        this.currentIndex = currentIndex;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (currentIndex < 0 || newIndex < 0) {
            return Status.CANCEL_STATUS;
        }
        return redo(monitor, info);
    }

    private void doMoveVariable(int currentIndex, int newIndex) {
        VariableEntity var = variableList.get(currentIndex);
        variableList.remove(currentIndex);
        variableList.add(newIndex, var);
        tableViewer.setSelection(new StructuredSelection(var));
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        doMoveVariable(currentIndex, newIndex);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        doMoveVariable(newIndex, currentIndex);
        return Status.OK_STATUS;
    }

}
