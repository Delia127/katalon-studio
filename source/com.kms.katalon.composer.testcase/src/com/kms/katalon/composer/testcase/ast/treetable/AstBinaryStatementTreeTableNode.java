package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstBinaryStatementTreeTableNode extends AstExpressionStatementTreeTableNode {
    public AstBinaryStatementTreeTableNode(ExpressionStatementWrapper expressionStatement, AstTreeTableNode parentNode) {
        super(expressionStatement, parentNode);
    }

    @Override
    public String getItemText() {
        return StringConstants.TREE_BINARY_STATEMENT;
    }

    @Override
    public Image getIcon() {
        return ImageConstants.IMG_16_BINARY;
    }

    @Override
    public boolean setInput(Object input) {
        if (input instanceof BinaryExpressionWrapper
                && !AstTreeTableValueUtil.compareAstNode(input, expressionStatement.getExpression())) {
            expressionStatement.setExpression((ExpressionWrapper) input);
            return true;
        }
        return false;
    }

    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return AstTreeTableValueUtil.getCellEditorForBinaryExpression(parent,
                (BinaryExpressionWrapper) expressionStatement.getExpression());
    }
}
