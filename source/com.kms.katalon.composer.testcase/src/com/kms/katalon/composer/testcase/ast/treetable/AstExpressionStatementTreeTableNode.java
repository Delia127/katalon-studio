package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstExpressionStatementTreeTableNode extends AstStatementTreeTableNode {
    protected ExpressionStatementWrapper expressionStatement;

    public AstExpressionStatementTreeTableNode(ExpressionStatementWrapper expressionStatement, AstTreeTableNode parentNode) {
        super(expressionStatement, parentNode);
        this.expressionStatement = expressionStatement;
    }

    @Override
    public ExpressionStatementWrapper getASTObject() {
        return expressionStatement;
    }

    @Override
    public String getInputText() {
        return expressionStatement.getExpression().getText();
    }

    @Override
    public boolean canEditInput() {
        return true;
    }

    @Override
    public String getInputTooltipText() {
        return getInputText();
    }

    @Override
    public Object getInput() {
        return expressionStatement.getExpression();
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return AstTreeTableValueUtil.getCellEditorForExpression(parent, expressionStatement.getExpression());
    }

    @Override
    public boolean setInput(Object input) {
        if (input instanceof ExpressionWrapper
                && !AstTreeTableValueUtil.compareAstNode(input, expressionStatement.getExpression())) {
            expressionStatement.setExpression((ExpressionWrapper) input);
            return true;
        }
        return false;
    }
}
