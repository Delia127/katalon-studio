package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.FieldExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class FieldExpressionWrapper extends ExpressionWrapper {
    private FieldNode fieldNode;

    public FieldExpressionWrapper(FieldNode fieldNode, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.fieldNode = fieldNode;
    }

    public FieldExpressionWrapper(FieldExpression fieldExpression, ASTNodeWrapper parentNodeWrapper) {
        super(fieldExpression, parentNodeWrapper);
        this.fieldNode = fieldExpression.getField();
    }

    public FieldExpressionWrapper(FieldExpressionWrapper fieldExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(fieldExpressionWrapper, parentNodeWrapper);
        this.fieldNode = fieldExpressionWrapper.getField();
    }

    @Override
    public String getText() {
        return fieldNode.getName();
    }

    public FieldNode getField() {
        return fieldNode;
    }

    @Override
    public ClassNodeWrapper getType() {
        return new ClassNodeWrapper(fieldNode.getType(), this);
    }

    @Override
    public FieldExpressionWrapper clone() {
        return new FieldExpressionWrapper(this, getParent());
    }
}
