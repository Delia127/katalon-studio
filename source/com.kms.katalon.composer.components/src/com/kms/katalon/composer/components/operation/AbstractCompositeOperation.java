package com.kms.katalon.composer.components.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class AbstractCompositeOperation extends AbstractOperation implements ICompositeOperation {
    private List<IUndoableOperation> operations = new ArrayList<>();

    public AbstractCompositeOperation(String label) {
        super(label);
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        for (IUndoableOperation operation : operations) {
            operation.execute(monitor, info);
        }
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        for (IUndoableOperation operation : operations) {
            operation.redo(monitor, info);
        }
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        List<IUndoableOperation> revertOperations = new ArrayList<>(operations);
        Collections.reverse(revertOperations);
        for (IUndoableOperation operation : revertOperations) {
            operation.undo(monitor, info);
        }
        return Status.OK_STATUS;
    }

    @Override
    public void add(IUndoableOperation operation) {
        if (operation == null) {
            return;
        }
        operations.add(operation);
    }

    @Override
    public void remove(IUndoableOperation operation) {
        if (operation == null) {
            return;
        }
        operations.remove(operation);
    }

}
