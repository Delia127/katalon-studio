package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class ArgumentListExpressionWrapper extends TupleExpressionWrapper {
    public ArgumentListExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ArgumentListExpressionWrapper(List<ExpressionWrapper> expressions, ASTNodeWrapper parentNodeWrapper) {
        super(expressions, parentNodeWrapper);
    }

    public ArgumentListExpressionWrapper(ArgumentListExpression argumentListExpression, ASTNodeWrapper parentNodeWrapper) {
        super(argumentListExpression, parentNodeWrapper);
    }

    public ArgumentListExpressionWrapper(ArgumentListExpressionWrapper argumentListExpression,
            ASTNodeWrapper parentNodeWrapper) {
        super(argumentListExpression, parentNodeWrapper);
    }

    @Override
    public String getText() {
        StringBuilder value = new StringBuilder();
        value.append("(");
        value.append(StringUtils.join(Iterables.transform(expressions, new Function<ExpressionWrapper, String>() {
            @Override
            public String apply(ExpressionWrapper expression) {
                return expression.getText();
            }
        }).iterator(), ", "));
        value.append(")");
        return value.toString();
    }

    @Override
    public ArgumentListExpressionWrapper clone() {
        return new ArgumentListExpressionWrapper(this, getParent());
    }
}
