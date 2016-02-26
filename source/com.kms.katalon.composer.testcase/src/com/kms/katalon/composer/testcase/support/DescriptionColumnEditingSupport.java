package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.TestStepDescriptionBuilderCellEditor;
import com.kms.katalon.composer.testcase.ast.treetable.AstCaseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCatchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstFinallyStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
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
        return (element instanceof AstStatementTreeTableNode && !(element instanceof AstElseStatementTreeTableNode)
                && !(element instanceof AstElseIfStatementTreeTableNode) && !(element instanceof AstCatchStatementTreeTableNode)
                && !(element instanceof AstFinallyStatementTreeTableNode) && !(element instanceof AstCaseStatementTreeTableNode));
    }

    @Override
    protected Object getValue(Object element) {
        return ((AstStatementTreeTableNode) element).getDescription();
    }

    @Override
    protected void setValue(Object element, Object value) {
        try {
            if (!(value instanceof String) || ((AstStatementTreeTableNode) element).getDescription().equals((String) value)) {
                return;
            }
            ((AstStatementTreeTableNode) element).setDescription((String) value);
            parentTestCasePart.getTreeTableInput().setDirty(true);
            parentTestCasePart.getTreeTableInput().refresh(element);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_DESCRIPTION);
        }
    }
}
