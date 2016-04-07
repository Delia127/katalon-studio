package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

public interface IAstInputEditableNode {
    public boolean canEditInput();
    public String getInputText();
    public String getInputTooltipText();
    public Object getInput();
    public boolean setInput(Object input);
    public CellEditor getCellEditorForInput(Composite parent);
}
