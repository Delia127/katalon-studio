package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class TupleExpressionWrapper extends ExpressionWrapper {
    protected List<ExpressionWrapper> expressions = new ArrayList<ExpressionWrapper>();

    public TupleExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public TupleExpressionWrapper(TupleExpression tupleExpression, ASTNodeWrapper parentNodeWrapper) {
        super(tupleExpression, parentNodeWrapper);
        for (Expression expression : tupleExpression.getExpressions()) {
            expressions.add(ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression, this));
        }
    }

    public TupleExpressionWrapper(TupleExpressionWrapper tupleExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(tupleExpressionWrapper, parentNodeWrapper);
        copyTupleProperties(tupleExpressionWrapper);
    }

    private void copyTupleProperties(TupleExpressionWrapper tupleExpressionWrapper) {
        for (ExpressionWrapper expression : tupleExpressionWrapper.getExpressions()) {
            expressions.add(expression.copy(this));
        }
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

    public ExpressionWrapper getExpression(int index) {
        if (index < 0 || index >= expressions.size()) {
            return null;
        }
        return this.expressions.get(index);
    }

    public void setExpressions(List<ExpressionWrapper> expressions) {
        if (expressions == null) {
            return;
        }
        this.expressions.clear();
        for (ExpressionWrapper expression : expressions) {
            if (expression == null) {
                continue;
            }
            expression.setParent(this);
            this.expressions.add(expression);
        }
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("(");
        boolean first = true;
        for (ExpressionWrapper expression : expressions) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }

            buffer.append(expression.getText());
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(expressions);
        return astNodeWrappers;
    }

    @Override
    public TupleExpressionWrapper clone() {
        return new TupleExpressionWrapper(this, getParent());
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
        if (!(input instanceof TupleExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyTupleProperties((TupleExpressionWrapper) input);
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        int index = getExpressions().indexOf(oldChild);
        if (newChild instanceof ExpressionWrapper && index >= 0 && index < getExpressions().size()) {
            setExpression((ExpressionWrapper) newChild, index);
            return true;
        }
        return super.replaceChild(oldChild, newChild);
    }
}
