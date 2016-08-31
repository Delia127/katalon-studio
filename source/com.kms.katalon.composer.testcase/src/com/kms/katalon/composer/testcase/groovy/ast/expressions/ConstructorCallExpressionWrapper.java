package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class ConstructorCallExpressionWrapper extends ExpressionWrapper {
    private ArgumentListExpressionWrapper arguments;

    public ConstructorCallExpressionWrapper(Class<?> type, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(type, this);
        this.arguments = new ArgumentListExpressionWrapper(this);
    }

    public ConstructorCallExpressionWrapper(ConstructorCallExpression constructorCallExpression,
            ASTNodeWrapper parentNodeWrapper) {
        super(constructorCallExpression, parentNodeWrapper);
        this.type = ClassNodeWrapper.getClassWrapper(constructorCallExpression.getType(), this);
        if (constructorCallExpression.getArguments() instanceof ArgumentListExpression) {
            arguments = new ArgumentListExpressionWrapper(
                    (ArgumentListExpression) constructorCallExpression.getArguments(), this);
        } else {
            arguments = new ArgumentListExpressionWrapper(this);
        }
    }

    public ConstructorCallExpressionWrapper(ConstructorCallExpressionWrapper constructorCallExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(constructorCallExpressionWrapper, parentNodeWrapper);
        this.type = constructorCallExpressionWrapper.getType();
        this.arguments = new ArgumentListExpressionWrapper(constructorCallExpressionWrapper.getArguments(), this);
    }

    public ArgumentListExpressionWrapper getArguments() {
        return arguments;
    }

    @Override
    public String getText() {
        String text = null;
        if (isSuperCall()) {
            text = "super ";
        } else if (isThisCall()) {
            text = "this ";
        } else {
            text = "new " + getType().getName();
        }
        return text + arguments.getText();
    }

    public boolean isSuperCall() {
        return getType().isSuper();
    }

    public boolean isSpecialCall() {
        return isThisCall() || isSuperCall();
    }

    public boolean isThisCall() {
        return getType().isThis();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        if (!isSpecialCall()) {
            astNodeWrappers.add(type);
        }
        astNodeWrappers.add(arguments);
        return astNodeWrappers;
    }

    @Override
    public ConstructorCallExpressionWrapper clone() {
        return new ConstructorCallExpressionWrapper(this, getParent());
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getType() && newChild instanceof ClassNodeWrapper) {
            setType((ClassNodeWrapper) newChild);
            return true;
        }
        return super.replaceChild(oldChild, newChild);
    }
}
