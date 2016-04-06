package com.kms.katalon.composer.testcase.support;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstObjectEditableNode;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestObjectEditingSupport extends EditingSupport {
    private TreeViewer treeViewer;
    private TestCasePart parentTestCasePart;

    public TestObjectEditingSupport(TreeViewer treeViewer, TestCasePart parentTestCasePart) {
        super(treeViewer);
        this.treeViewer = treeViewer;
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return ((AstObjectEditableNode) element).getCellEditorForTestObject(treeViewer.getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
        return (element instanceof AstObjectEditableNode && ((AstObjectEditableNode) element).canEditTestObject());
    }

    @Override
    protected Object getValue(Object element) {
        return ((AstObjectEditableNode) element).getTestObject();
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!((AstObjectEditableNode) element).setTestObject(value)) {
            return;
        }
        if (element instanceof AstCallTestCaseKeywordTreeTableNode) {
            List<VariableEntity> testCaseVariables = ((AstCallTestCaseKeywordTreeTableNode) element).getCallTestCaseVariables();
            parentTestCasePart.addVariables(testCaseVariables.toArray(new VariableEntity[testCaseVariables.size()]));
        }
        parentTestCasePart.getTreeTableInput().setDirty(true);
        treeViewer.refresh(element);
    }
}
