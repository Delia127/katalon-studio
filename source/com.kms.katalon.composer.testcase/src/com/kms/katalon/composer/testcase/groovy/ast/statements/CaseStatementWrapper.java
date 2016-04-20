package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class CaseStatementWrapper extends ComplexChildStatementWrapper {
    protected ExpressionWrapper expression;

    public CaseStatementWrapper(CaseStatement caseStatement, SwitchStatementWrapper parentSwitchStatement) {
        super(caseStatement, (BlockStatement) caseStatement.getCode(), parentSwitchStatement);
        this.lastLineNumber = caseStatement.getCode().getLastLineNumber();
        this.lastColumnNumber = caseStatement.getCode().getColumnNumber();
        this.end = caseStatement.getEnd();
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(caseStatement.getExpression(), this);
    }

    public CaseStatementWrapper(CaseStatementWrapper caseStatementWrapper, SwitchStatementWrapper parentSwitchStatement) {
        super(caseStatementWrapper, parentSwitchStatement);
        this.expression = caseStatementWrapper.getExpression().copy(this);
    }

    public CaseStatementWrapper(SwitchStatementWrapper parentSwitchStatement) {
        super(parentSwitchStatement);
        this.expression = new BooleanExpressionWrapper(this);
        block.addStatement(new BreakStatementWrapper(block));
    }

    public CaseStatementWrapper() {
        this(null);
    }

    public void setExpression(ExpressionWrapper expression) {
        if (expression == null) {
            return;
        }
        expression.setParent(this);
        this.expression = expression;
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("case ");
        stringBuilder.append(getExpression().getText());
        stringBuilder.append(":");
        return stringBuilder.toString();
    }

    @Override
    public SwitchStatementWrapper getParent() {
        return (SwitchStatementWrapper) super.getParent();
    }

    @Override
    public CaseStatementWrapper clone() {
        return new CaseStatementWrapper(this, getParent());
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
        return this.getInput().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof CaseStatementWrapper
                && !this.getExpression().isEqualsTo(((CaseStatementWrapper) input).getExpression())) {
            this.setExpression(((CaseStatementWrapper) input).getExpression());
            return true;
        }
        return false;
    }

    @Override
    protected boolean isAstNodeBelongToParentComplex(ASTNodeWrapper astNode) {
        return astNode instanceof CaseStatementWrapper || astNode instanceof DefaultStatementWrapper;
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
