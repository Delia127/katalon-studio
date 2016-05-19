package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class ListExpressionWrapper extends ExpressionWrapper {
    protected List<ExpressionWrapper> expressions = new ArrayList<ExpressionWrapper>();
    protected boolean wrapped = false;

    public ListExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(ClassHelper.LIST_TYPE, this);
    }

    public ListExpressionWrapper(List<ExpressionWrapper> expressions, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expressions = expressions;
        this.type = new ClassNodeWrapper(ClassHelper.LIST_TYPE, this);
    }

    public ListExpressionWrapper(ListExpression listExpression, ASTNodeWrapper parentNodeWrapper) {
        super(listExpression, parentNodeWrapper);
        for (Expression childExpression : listExpression.getExpressions()) {
            expressions.add(ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(childExpression, this));
        }
        wrapped = listExpression.isWrapped();
    }

    public ListExpressionWrapper(ListExpressionWrapper listExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(listExpressionWrapper, parentNodeWrapper);
        copyListProperties(listExpressionWrapper);
    }

    private void copyListProperties(ListExpressionWrapper listExpressionWrapper) {
        for (ExpressionWrapper childExpression : listExpressionWrapper.getExpressions()) {
            expressions.add(childExpression.copy(this));
        }
        wrapped = listExpressionWrapper.isWrapped();
    }

    public List<ExpressionWrapper> getExpressions() {
        return Collections.unmodifiableList(expressions);
    }
    
    public void addExpression(ExpressionWrapper expression) {
        if (expression == null) {
            return;
        }
        expression.setParent(this);
        expressions.add(expression);
    }

    public boolean addExpression(ExpressionWrapper expression, int index) {
        if (expression == null || index < 0 || index > expressions.size()) {
            return false;
        }
        expression.setParent(this);
        expressions.add(index, expression);
        return true;
    }

    public void addExpressions(List<ExpressionWrapper> listOfExpressions) {
        if (listOfExpressions == null) {
            return;
        }
        for (ExpressionWrapper expression : listOfExpressions) {
            if (expression == null) {
                continue;
            }
            expression.setParent(this);
            expressions.add(expression);
        }
    }
    
    public boolean removeExpression(ExpressionWrapper expression) {
        return expressions.remove(expression);
    }
    
    public boolean removeExpression(int index) {
        if (index < 0 || index >= expressions.size()) {
            return false;
        }
        expressions.remove(index);
        return true;
    }
    
    public boolean setExpression(ExpressionWrapper expression, int index) {
        if (expression == null || index < 0 || index > expressions.size()) {
            return false;
        }
        expression.setParent(this);
        expressions.set(index, expression);
        return true;
    }

    public boolean isWrapped() {
        return wrapped;
    }

    @Override
    public String getText() {
        StringBuilder value = new StringBuilder();
        value.append("[");
        value.append(StringUtils.join(Iterables.transform(expressions, new Function<ExpressionWrapper, String>() {
            @Override
            public String apply(ExpressionWrapper expression) {
                return expression.getText();
            }
        }).iterator(), ", "));
        value.append("]");
        return value.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(expressions);
        return astNodeWrappers;
    }

    @Override
    public ListExpressionWrapper clone() {
        return new ListExpressionWrapper(this, getParent());
    }
    
    @Override
    public boolean isInputEditatble() {
        return true;
    }
    
    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }
    
    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof ListExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyListProperties((ListExpressionWrapper) input);
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        int index = getExpressions().indexOf(oldChild);
        if (newChild instanceof ExpressionWrapper && index >= 0 && index < getExpressions().size()) {
            setExpression((ExpressionWrapper) newChild, index);
            return true;
        }
        return false;
    }
    
}
