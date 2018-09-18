package com.kms.katalon.composer.testcase.providers;

import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.TableViewer;


public interface XpathTableActionOperator {
    
    TableViewer getTableViewer();
    
    IStatus executeOperation(IUndoableOperation operation);
    
    List<String> getStringList();
    
    void setDirty(boolean dirty);
}
