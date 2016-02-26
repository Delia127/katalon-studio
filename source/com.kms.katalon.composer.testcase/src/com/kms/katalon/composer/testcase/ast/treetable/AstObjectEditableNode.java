package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

public interface AstObjectEditableNode {
    public boolean canEditTestObject();
    public String getTestObjectText();
    public String getTestObjectTooltipText();
    public Object getTestObject();
    public boolean setTestObject(Object object);
    public CellEditor getCellEditorForTestObject(Composite parent);
}
