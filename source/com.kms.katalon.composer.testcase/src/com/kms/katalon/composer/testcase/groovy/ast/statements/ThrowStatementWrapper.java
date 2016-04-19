package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.ThrowStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.core.exception.StepFailedException;

public class ThrowStatementWrapper extends StatementWrapper {
    public static final Class<?> DEFAULT_THROW_TYPE = StepFailedException.class;

    private ExpressionWrapper expression;
    
    public ThrowStatementWrapper() {
        this(null);
    }

    public ThrowStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        expression = new ConstructorCallExpressionWrapper(DEFAULT_THROW_TYPE, this);
    }

    public ThrowStatementWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
    }

    public ThrowStatementWrapper(ThrowStatement throwStatement, ASTNodeWrapper parentNodeWrapper) {
        super(throwStatement, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(throwStatement.getExpression(), this);
    }

    public ThrowStatementWrapper(ThrowStatementWrapper throwStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(throwStatementWrapper, parentNodeWrapper);
        this.expression = throwStatementWrapper.getExpression().copy(this);
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        if (expression == null) {
            return;
        }
        expression.setParent(this);
        this.expression = expression;
    }

    @Override
    public String getText() {
        return "throw " + getInputText();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        return astNodeWrappers;
    }

    @Override
    public ThrowStatementWrapper clone() {
        return new ThrowStatementWrapper(this, getParent());
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
    public String getInputText() {
        return getExpression().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof ThrowStatementWrapper
                && !getExpression().isEqualsTo(((ThrowStatementWrapper) input).getExpression())) {
            setExpression(((ThrowStatementWrapper) input).getExpression());
            return true;
        }
        return false;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getExpression() && newChild instanceof ExpressionWrapper) {
            setExpression((ExpressionWrapper) newChild);
            return true;
        }
        return super.replaceChild(oldChild, newChild);
    }
}
