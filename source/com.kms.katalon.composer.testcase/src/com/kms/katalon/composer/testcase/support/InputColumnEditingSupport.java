package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.treetable.AstCatchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstForStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
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
        if (element instanceof AstTreeTableNode) {
            return ((AstTreeTableNode) element).getCellEditorForInput(treeViewer.getTree());
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (element instanceof AstTreeTableNode) {
            return ((AstTreeTableNode) element).isInputEditable();
        }
        return false;
    }

    @Override
    protected Object getValue(Object element) {
        if (element instanceof AstTreeTableNode) {
            return ((AstTreeTableNode) element).getInput();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof AstTreeTableNode && ((AstTreeTableNode) element).setInput(value)) {
            try {
                parentTestCasePart.getTreeTableInput().setDirty(true);
                if (element instanceof AstForStatementTreeTableNode
                        || element instanceof AstCatchStatementTreeTableNode) {
                    parentTestCasePart.getTreeTableInput().refresh(((AstTreeTableNode) element).getParent());
                } else {
                    parentTestCasePart.getTreeTableInput().refresh(element);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }
}
