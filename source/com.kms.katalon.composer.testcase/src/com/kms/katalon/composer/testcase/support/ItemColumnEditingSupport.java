package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.testcase.ast.treetable.IAstItemEditableNode;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class ItemColumnEditingSupport extends EditingSupport {
    private TreeViewer treeViewer;

    private TestCasePart parentTestCasePart;

    public ItemColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
        super(viewer);
        this.treeViewer = viewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element instanceof IAstItemEditableNode) {
            return ((IAstItemEditableNode) element).getCellEditorForItem(treeViewer.getTree());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof IAstItemEditableNode && ((IAstItemEditableNode) element).canEditItem());
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof IAstItemEditableNode) {
            return ((IAstItemEditableNode) element).getItem();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(element instanceof IAstItemEditableNode) || !((IAstItemEditableNode) element).setItem(value)) {
            return;
        }
        parentTestCasePart.getTreeTableInput().setDirty(true);
        treeViewer.refresh(element);
    }
}
