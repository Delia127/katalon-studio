package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;


public interface IAstItemEditableNode {
    public boolean canEditItem();
    public Object getItem();
    public boolean setItem(Object item);
    public CellEditor getCellEditorForItem(Composite parent);
}
