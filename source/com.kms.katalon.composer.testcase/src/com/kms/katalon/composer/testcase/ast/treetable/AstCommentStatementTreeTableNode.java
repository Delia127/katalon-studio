package com.kms.katalon.composer.testcase.ast.treetable;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AstCommentStatementTreeTableNode extends AstExpressionStatementTreeTableNode {

    public AstCommentStatementTreeTableNode(ExpressionStatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode);
    }

    @Override
    public String getItemText() {
        return StringConstants.TREE_COMMENT;
    }

    @Override
    public Image getIcon() {
        return ImageConstants.IMG_16_COMMENT;
    }
    
    @Override
    public CellEditor getCellEditorForInput(Composite parent) {
        return new TextCellEditor(parent);
    }

    @Override
    public boolean setInput(Object input) {
        if (input instanceof MethodCallExpressionWrapper
                && !AstTreeTableValueUtil.compareAstNode(input, expressionStatement.getExpression())) {
            expressionStatement.setExpression((ExpressionWrapper) input);
            return true;
        }
        return false;
    }
}
