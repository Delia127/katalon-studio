package com.kms.katalon.composer.testcase.parts;

import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.TableViewer;

import com.kms.katalon.entity.variable.VariableEntity;

public interface VariableTableActionOperator {
    
    TableViewer getTableViewer();
    
    IStatus executeOperation(IUndoableOperation operation);
    
    List<VariableEntity> getVariablesList();
    
    void setDirty(boolean dirty);
}
