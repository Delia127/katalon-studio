package com.kms.katalon.composer.testcase.model;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public interface InputValueEditorProvider {
    String getName();
    
    boolean isEditable(Object astObject);
    
    CellEditor getCellEditorForValue(Composite parent, Object astObject);
    
    Object newValue();
    
    Object getValueToEdit(Object rawValue);
    
    String getValueToDisplay(Object rawValue);
    
    ASTNodeWrapper toASTNodeWrapper(Object rawValue);
}
