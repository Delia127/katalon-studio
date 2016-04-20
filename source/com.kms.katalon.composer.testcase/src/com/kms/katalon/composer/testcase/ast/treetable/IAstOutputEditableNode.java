package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

public interface IAstOutputEditableNode {
    public boolean canEditOutput();
    public String getOutputText();
    public String getOutputTooltipText();
    public Object getOutput();
    public boolean setOutput(Object output);
    public CellEditor getCellEditorForOutput(Composite parent);
}
