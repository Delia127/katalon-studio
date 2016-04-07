package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;

public class BinaryExpressionWrapper extends ExpressionWrapper {
    protected ExpressionWrapper leftExpression;

    protected ExpressionWrapper rightExpression;

    protected TokenWrapper operation;

    public BinaryExpressionWrapper() {
        this(null);
    }

    public BinaryExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.leftExpression = new ConstantExpressionWrapper(0, this);
        this.operation = new TokenWrapper(Token.newSymbol(Types.COMPARE_EQUAL, -1, -1), this);
        this.rightExpression = new ConstantExpressionWrapper(0, this);
    }

    public BinaryExpressionWrapper(String variableName, Token operation, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.leftExpression = new ConstantExpressionWrapper(0, this);
        this.operation = new TokenWrapper(Token.newSymbol(Types.COMPARE_EQUAL, -1, -1), this);
        this.rightExpression = new ConstantExpressionWrapper(0, this);
    }

    public BinaryExpressionWrapper(BinaryExpressionWrapper binaryExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(binaryExpressionWrapper, parentNodeWrapper);
        copyBinaryProperties(binaryExpressionWrapper);
    }

    private void copyBinaryProperties(BinaryExpressionWrapper binaryExpressionWrapper) {
        leftExpression = binaryExpressionWrapper.getLeftExpression().copy(this);
        rightExpression = binaryExpressionWrapper.getRightExpression().copy(this);
        operation = new TokenWrapper(binaryExpressionWrapper.getOperation(), this);
    }

    public BinaryExpressionWrapper(BinaryExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        leftExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getLeftExpression(), this);
        rightExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(expression.getRightExpression(),
                this);
        this.operation = new TokenWrapper(expression.getOperation(), this);
    }

    public ExpressionWrapper getLeftExpression() {
        return leftExpression;
    }

    public void setLeftExpression(ExpressionWrapper leftExpression) {
        if (leftExpression == null) {
            return;
        }
        leftExpression.setParent(this);
        this.leftExpression = leftExpression;
    }

    public ExpressionWrapper getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(ExpressionWrapper rightExpression) {
        if (rightExpression == null) {
            return;
        }
        rightExpression.setParent(this);
        this.rightExpression = rightExpression;
    }

    public TokenWrapper getOperation() {
        return operation;
    }

    public void setOperation(TokenWrapper operation) {
        if (operation == null) {
            return;
        }
        operation.setParent(this);
        this.operation = operation;
    }

    @Override
    public String getText() {
        if (operation.getType() == Types.LEFT_SQUARE_BRACKET) {
            return leftExpression.getText() + "[" + rightExpression.getText() + "]";
        }
        return leftExpression.getText() + " " + operation.getText() + " " + rightExpression.getText();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(leftExpression);
        astNodeWrappers.add(operation);
        astNodeWrappers.add(rightExpression);
        return astNodeWrappers;
    }

    @Override
    public BinaryExpressionWrapper clone() {
        return new BinaryExpressionWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof BinaryExpressionWrapper) || isEqualsTo(input)) {
            return false;
        }
        copyBinaryProperties((BinaryExpressionWrapper) input);
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getLeftExpression() && newChild instanceof ExpressionWrapper) {
            setLeftExpression((ExpressionWrapper) newChild);
            return true;
        } else if (oldChild == getRightExpression() && newChild instanceof ExpressionWrapper) {
            setRightExpression((ExpressionWrapper) newChild);
            return true;
        } else if (oldChild == getOperation() && newChild instanceof TokenWrapper) {
            setOperation((TokenWrapper) newChild);
            return true;
        }
        return false;
    }
}
