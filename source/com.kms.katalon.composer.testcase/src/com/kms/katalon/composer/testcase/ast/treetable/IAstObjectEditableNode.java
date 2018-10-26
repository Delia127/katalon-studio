package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.parts.ITestCasePart;

public interface IAstObjectEditableNode {
    boolean canEditTestObject();
    String getTestObjectText();
    String getTestObjectTooltipText();
    Object getTestObject();
    boolean setTestObject(Object object);
    CellEditor getCellEditorForTestObject(Composite parent);
    void setTestCasePart(ITestCasePart testCasePart);
    ITestCasePart getTestCasePart();
} 