package com.kms.katalon.composer.testcase.support;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.IAstInputEditableNode;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;

public class InputColumnEditingSupport extends TypeCheckedEditingSupport<IAstInputEditableNode> {

    private TestCasePart parentTestCasePart;

    public InputColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
        super(viewer);
        this.parentTestCasePart = parentTestCasePart;
    }

    @Override
    protected Class<IAstInputEditableNode> getElementType() {
        return IAstInputEditableNode.class;
    }

    @Override
    protected CellEditor getCellEditorByElement(IAstInputEditableNode element) {
        return element.getCellEditorForInput(getComposite());
    }

    @Override
    protected boolean canEditElement(IAstInputEditableNode element) {
        return element.canEditInput();
    }

    @Override
    protected Object getElementValue(IAstInputEditableNode element) {
        return element.getInput();
    }

    @Override
    protected void setElementValue(IAstInputEditableNode element, Object value) {
        if (!element.setInput(value)) {
            return;
        }

        TestCaseTreeTableInput treeTableInput = parentTestCasePart.getTreeTableInput();
        treeTableInput.setDirty(true);
        if (isNodeTransformed(element, value)) {
            reloadTableIfNodeTransformed(element, treeTableInput);
            return;
        }
        treeTableInput.refresh(element);
    }

    private void reloadTableIfNodeTransformed(IAstInputEditableNode element, TestCaseTreeTableInput treeTableInput) {
        AstTreeTableNode parentNode = ((AstTreeTableNode) element).getParent();
        if (parentNode != null) {
            treeTableInput.refresh(parentNode);
            return;
        }
        treeTableInput.reloadTreeTableNodes();
    }

    private boolean isNodeTransformed(IAstInputEditableNode element, Object value) {
        if (!(element instanceof AstStatementTreeTableNode)) {
            return false;
        }
        AstStatementTreeTableNode statementTreeTableNode = (AstStatementTreeTableNode) element;
        List<AstTreeTableNode> newNodes = WrapperToAstTreeConverter.getInstance()
                .convert(Arrays.asList(new StatementWrapper[] { statementTreeTableNode.getASTObject() }),
                        statementTreeTableNode);
        if (newNodes.size() != 1) {
            return true;
        }
        AstTreeTableNode newElementNode = newNodes.get(0);
        return !element.equals(newElementNode) || !element.getClass().equals(newElementNode.getClass());
    }
}
