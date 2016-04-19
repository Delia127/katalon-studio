package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.testcase.ast.editors.TestStepDescriptionBuilderCellEditor;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.parts.TestCasePart;

public class DescriptionColumnEditingSupport extends EditingSupport {
    private TreeViewer treeViewer;

    private TestCasePart parentTestCasePart;

    public DescriptionColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
        super(viewer);
        this.treeViewer = viewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new TestStepDescriptionBuilderCellEditor(treeViewer.getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
        return element instanceof AstStatementTreeTableNode
                && ((AstStatementTreeTableNode) element).canHaveDescription();
    }

    @Override
    protected Object getValue(Object element) {
        return ((AstStatementTreeTableNode) element).getDescription();
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!(value instanceof String) || !((AstStatementTreeTableNode) element).setDescription((String) value)) {
            return;
        }
        parentTestCasePart.getTreeTableInput().setDirty(true);
        parentTestCasePart.getTreeTableInput().refresh(element);
    }
}
