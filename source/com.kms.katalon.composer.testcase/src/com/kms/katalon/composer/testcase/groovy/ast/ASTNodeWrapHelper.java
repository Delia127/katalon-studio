package com.kms.katalon.composer.testcase.groovy.ast;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;

public class ASTNodeWrapHelper {
    public static StatementWrapper getStatementNodeWrapperFromStatement(Statement statement, ASTNodeWrapper parent) {
        if (statement == null) {
            return null;
        }
        return StatementWrapHelper.wrap(statement, parent);
    }

    public static ExpressionWrapper getExpressionNodeWrapperFromExpression(Expression expression, ASTNodeWrapper parent) {
        if (expression == null) {
            return null;
        }
        return ExpressionWrapHelper.wrap(expression, parent);
    }
}
