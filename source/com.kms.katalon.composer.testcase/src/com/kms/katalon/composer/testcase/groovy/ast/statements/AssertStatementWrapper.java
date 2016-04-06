package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.AssertStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class AssertStatementWrapper extends StatementWrapper {
    private BooleanExpressionWrapper booleanExpression;
    private ExpressionWrapper messageExpression;

    public AssertStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.booleanExpression = new BooleanExpressionWrapper(this);
    }

    public AssertStatementWrapper(AssertStatement assertStatement, ASTNodeWrapper parentNodeWrapper) {
        super(assertStatement, parentNodeWrapper);
        booleanExpression = new BooleanExpressionWrapper(assertStatement.getBooleanExpression(), this);
        messageExpression = ASTNodeWrapHelper
                .getExpressionNodeWrapperFromExpression(assertStatement.getMessageExpression(), this);
    }

    public AssertStatementWrapper(AssertStatementWrapper assertStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(assertStatementWrapper, parentNodeWrapper);
        booleanExpression = new BooleanExpressionWrapper(assertStatementWrapper.getBooleanExpression(), this);
        messageExpression = assertStatementWrapper.getMessageExpression().copy(this);
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return booleanExpression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    public ExpressionWrapper getMessageExpression() {
        return messageExpression;
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("assert ");
        stringBuilder.append(getBooleanExpression().getText());
        if (getMessageExpression() instanceof ConstantExpressionWrapper
                && !(((ConstantExpressionWrapper) getMessageExpression()).getValue() == null)) {
            stringBuilder.append(" : ");
            stringBuilder.append(getMessageExpression().getText());
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(booleanExpression);
        astNodeWrappers.add(messageExpression);
        return astNodeWrappers;
    }

    @Override
    public AssertStatementWrapper clone() {
        return new AssertStatementWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this.getBooleanExpression();
    }

    @Override
    public String getInputText() {
        if (this.getBooleanExpression() != null) {
            return this.getBooleanExpression().getText();
        }
        return "";
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof BooleanExpressionWrapper
                && !AstTreeTableValueUtil.compareAstNode(input, this.getBooleanExpression())) {
            this.setBooleanExpression((BooleanExpressionWrapper) input);
            return true;
        }
        return false;
    }
}
