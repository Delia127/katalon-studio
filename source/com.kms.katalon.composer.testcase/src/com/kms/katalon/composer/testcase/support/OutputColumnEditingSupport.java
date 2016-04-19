package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.testcase.ast.treetable.IAstOutputEditableNode;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class OutputColumnEditingSupport extends EditingSupport {

    private TreeViewer treeViewer;
    private TestCasePart parentTestCasePart;

    public OutputColumnEditingSupport(TreeViewer treeViewer, TestCasePart parentTestCasePart) {
        super(treeViewer);
        this.treeViewer = treeViewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element instanceof IAstOutputEditableNode) {
            return ((IAstOutputEditableNode) element).getCellEditorForOutput(treeViewer.getTree());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof IAstOutputEditableNode && ((IAstOutputEditableNode) element).canEditOutput());
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof IAstOutputEditableNode) {
            return ((IAstOutputEditableNode) element).getOutput();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof IAstOutputEditableNode && ((IAstOutputEditableNode) element).setOutput(value)) {
            parentTestCasePart.getTreeTableInput().setDirty(true);
            treeViewer.refresh(element);
        }
    }
}
