package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.codehaus.groovy.ast.expr.ClassExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class ClassExpressionWrapper extends ExpressionWrapper {

    public ClassExpressionWrapper(Class<?> clazz, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(clazz, this);
    }

    public ClassExpressionWrapper(ClassExpression classExpression, ASTNodeWrapper parentNodeWrapper) {
        super(classExpression, parentNodeWrapper);
        this.type = new ClassNodeWrapper(classExpression.getType(), this);
    }

    public ClassExpressionWrapper(ClassExpressionWrapper classExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(classExpressionWrapper, parentNodeWrapper);
        this.type = new ClassNodeWrapper(classExpressionWrapper.getType(), this);
    }
    
    @Override
    public String getText() {
        return type.getName();
    }

    @Override
    public ClassExpressionWrapper clone() {
        return new ClassExpressionWrapper(this, getParent());
    }

}
