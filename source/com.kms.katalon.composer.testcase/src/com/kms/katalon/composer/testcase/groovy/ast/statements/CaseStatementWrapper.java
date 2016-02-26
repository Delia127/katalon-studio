package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class CaseStatementWrapper extends CompositeStatementWrapper {
    private BlockStatementWrapper code;
    private ExpressionWrapper expression;
    
    public CaseStatementWrapper(CaseStatement caseStatement, SwitchStatementWrapper parentSwitchStatement) {
        super(caseStatement, parentSwitchStatement);
        this.code = new BlockStatementWrapper((BlockStatement) caseStatement.getCode(), this);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(caseStatement.getExpression(), this);
        this.lastLineNumber = caseStatement.getCode().getLastLineNumber();
        this.lastColumnNumber = caseStatement.getCode().getColumnNumber();
        this.end = caseStatement.getEnd();
    }

    public CaseStatementWrapper(CaseStatementWrapper caseStatementWrapper, SwitchStatementWrapper parentSwitchStatement) {
        super(caseStatementWrapper, parentSwitchStatement);
        this.code = new BlockStatementWrapper(caseStatementWrapper.getBlock(), this);
        this.expression = caseStatementWrapper.getExpression().copy(this);
    }

    public CaseStatementWrapper(SwitchStatementWrapper parentSwitchStatement) {
        super(parentSwitchStatement);
        this.expression = new BooleanExpressionWrapper(this);
        this.code = new BlockStatementWrapper(this);
        this.code.addStatement(new BreakStatementWrapper(code));
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        this.expression = expression;
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
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.add(code);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    @Override
    public CaseStatementWrapper clone() {
        return new CaseStatementWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this.expression;
    }

    @Override
    public String getInputText() {
        return this.getInput().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof CaseStatementWrapper
                && !AstTreeTableValueUtil.compareAstNode(((CaseStatementWrapper) input).getExpression(),
                this.getExpression())) {
            this.setExpression(((CaseStatementWrapper) input).getExpression());
            return true;
        }

        return false;
    }
}
