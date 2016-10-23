package com.kms.katalon.composer.components.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class OperationExecutor {
    private IUndoContext undoContext;

    private IOperationHistory operationHistory;

    public OperationExecutor(Object parentPart) {
        this.undoContext = new ObjectUndoContext(parentPart);
    }

    private IOperationHistory getOperationHistory() {
        if (operationHistory == null) {
            operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
        }
        return operationHistory;
    }

    public IUndoContext getUndoContext() {
        return undoContext;
    }

    public IStatus executeOperation(IUndoableOperation operation, IProgressMonitor progressMonitor, IAdaptable adaptable) {
        IOperationHistory operationHistory = getOperationHistory();
        try {
            operation.addContext(undoContext);
            return operationHistory.execute(operation, progressMonitor, adaptable);
        } catch (ExecutionException e) {
            LoggerSingleton.logError(e);
        }
        return Status.CANCEL_STATUS;
    }

    public IStatus executeOperation(IUndoableOperation operation, IAdaptable adaptable) {
        return executeOperation(operation, new NullProgressMonitor(), adaptable);
    }

    public IStatus executeOperation(IUndoableOperation operation) {
        return executeOperation(operation, null);
    }

    public IStatus undoOperation(IAdaptable adaptable, IProgressMonitor progressMonitor) {
        IOperationHistory operationHistory = getOperationHistory();
        try {
            return operationHistory.undo(undoContext, progressMonitor, adaptable);
        } catch (ExecutionException e) {
            LoggerSingleton.logError(e);
        }
        return Status.CANCEL_STATUS;
    }

    public IStatus undoOperation(IAdaptable adaptable) {
        return undoOperation(adaptable, new NullProgressMonitor());
    }

    public IStatus undoOperation() {
        return undoOperation(null);
    }

    public IStatus redoOperation(IAdaptable adaptable, IProgressMonitor progressMonitor) {
        IOperationHistory operationHistory = getOperationHistory();
        try {
            return operationHistory.redo(undoContext, progressMonitor, adaptable);
        } catch (ExecutionException e) {
            LoggerSingleton.logError(e);
        }
        return Status.CANCEL_STATUS;
    }

    public IStatus redoOperation(IAdaptable adaptable) {
        return redoOperation(adaptable, new NullProgressMonitor());
    }

    public IStatus redoOperation() {
        return redoOperation(null);
    }

}
