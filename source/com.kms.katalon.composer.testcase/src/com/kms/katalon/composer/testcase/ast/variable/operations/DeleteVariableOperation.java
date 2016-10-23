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

import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;
import com.kms.katalon.entity.variable.VariableEntity;

public class DeleteVariableOperation extends AbstractOperation {
    public class DeletedVariable {
        private VariableEntity variable;

        private int originalIndex;

        public DeletedVariable(VariableEntity variable, int originalIndex) {
            this.variable = variable;
            this.originalIndex = originalIndex;
        }

        public VariableEntity getVariable() {
            return variable;
        }

        public void setVariable(VariableEntity variable) {
            this.variable = variable;
        }

        public int getOriginalIndex() {
            return originalIndex;
        }

        public void setOriginalIndex(int originalIndex) {
            this.originalIndex = originalIndex;
        }
    }

    private TestCaseVariablePart testCaseVariablePart;

    private TableViewer tableViewer;

    private List<VariableEntity> variableList;

    private List<DeletedVariable> deletedVariableList;

    public DeleteVariableOperation(TestCaseVariablePart testCaseVariablePart) {
        super(DeleteVariableOperation.class.getName());
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
        this.variableList = testCaseVariablePart.getVariablesList();
        this.deletedVariableList = new ArrayList<DeletedVariable>();
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        deletedVariableList.clear();
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        Object[] selectionElements = selection.toArray();
        if (selectionElements.length == 0) {
            return Status.CANCEL_STATUS;
        }
        for (Object object : selectionElements) {
            if (object instanceof VariableEntity) {
                VariableEntity variable = (VariableEntity) object;
                int index = variableList.indexOf(variable);
                variableList.remove(index);
                deletedVariableList.add(new DeletedVariable(variable, index));
            }
        }
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        for (DeletedVariable deletedVariable : deletedVariableList) {
            variableList.remove(deletedVariable.getVariable());
        }
        tableViewer.refresh();
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        List<VariableEntity> undoElement = new ArrayList<VariableEntity>();
        for (DeletedVariable deletedVariable : deletedVariableList) {
            VariableEntity variable = deletedVariable.getVariable();
            variableList.add(deletedVariable.getOriginalIndex(), variable);
            undoElement.add(variable);
        }
        tableViewer.refresh();
        tableViewer.setSelection(new StructuredSelection(undoElement.toArray()));
        testCaseVariablePart.setDirty(true);
        return Status.OK_STATUS;
    }

}
