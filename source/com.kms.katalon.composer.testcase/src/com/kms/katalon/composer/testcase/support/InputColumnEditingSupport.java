package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.treetable.IAstInputEditableNode;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class InputColumnEditingSupport extends EditingSupport {
    private TreeViewer treeViewer;
    private TestCasePart parentTestCasePart;

    public InputColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
        super(viewer);
        this.treeViewer = viewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return ((IAstInputEditableNode) element).getCellEditorForInput(treeViewer.getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof IAstInputEditableNode && ((IAstInputEditableNode) element).canEditInput());
    }

    @Override
    protected Object getValue(Object element) {
        return ((IAstInputEditableNode) element).getInput();
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!((IAstInputEditableNode) element).setInput(value)) {
            return;
        }
        try {
            parentTestCasePart.getTreeTableInput().setDirty(true);
            parentTestCasePart.getTreeTableInput().refresh(element);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
