package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class SwitchStatementWrapper extends StatementWrapper {
    private ExpressionWrapper expression;
    private List<CaseStatementWrapper> caseStatements = new ArrayList<CaseStatementWrapper>();
    private BlockStatementWrapper defaultStatement = null;

    public SwitchStatementWrapper(ExpressionWrapper expression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = expression;
    }

    public SwitchStatementWrapper(SwitchStatement switchStatement, ASTNodeWrapper parentNodeWrapper) {
        super(switchStatement, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(switchStatement.getExpression(), this);
        for (CaseStatement caseStatement : switchStatement.getCaseStatements()) {
            caseStatements.add(new CaseStatementWrapper(caseStatement, this));
        }
        if (switchStatement.getDefaultStatement() instanceof BlockStatement) {
            defaultStatement = new BlockStatementWrapper((BlockStatement) switchStatement.getDefaultStatement(), this);
        }
    }

    public SwitchStatementWrapper(SwitchStatementWrapper switchStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(switchStatementWrapper, parentNodeWrapper);
        this.expression = switchStatementWrapper.getExpression().copy(this);
        for (CaseStatementWrapper caseStatement : switchStatementWrapper.getCaseStatements()) {
            caseStatements.add(new CaseStatementWrapper(caseStatement, this));
        }
        if (switchStatementWrapper.getDefaultStatement() != null) {
            defaultStatement = new BlockStatementWrapper(switchStatementWrapper.getDefaultStatement(), this);
        }
    }

    public SwitchStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(this);
        defaultStatement = new BlockStatementWrapper(this);
        defaultStatement.addStatement(new BreakStatementWrapper(defaultStatement));
    }

    public ExpressionWrapper getExpression() {
        return expression;
    }

    public void setExpression(ExpressionWrapper expression) {
        this.expression = expression;
    }

    public List<CaseStatementWrapper> getCaseStatements() {
        return caseStatements;
    }

    public void setCaseStatements(List<CaseStatementWrapper> caseStatements) {
        this.caseStatements = caseStatements;
    }

    public void addCaseStatement(CaseStatementWrapper caseStatement) {
        caseStatements.add(caseStatement);
    }

    public boolean addCaseStatement(CaseStatementWrapper caseStatement, int index) {
        if (index < 0 || index > caseStatements.size()) {
            return false;
        }
        caseStatements.add(index, caseStatement);
        return true;
    }

    public boolean removeCaseStatement(int index) {
        if (index < 0 || index >= caseStatements.size()) {
            return false;
        }
        caseStatements.remove(index);
        return true;
    }

    public boolean removeCaseStatement(CaseStatementWrapper caseStatement) {
        return caseStatements.remove(caseStatement);
    }

    public BlockStatementWrapper getDefaultStatement() {
        return defaultStatement;
    }

    public void setDefaultStatement(BlockStatementWrapper defaultStatement) {
        this.defaultStatement = defaultStatement;
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("switch (");
        stringBuilder.append(getExpression().getText());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.addAll(caseStatements);
        if (defaultStatement != null) {
            astNodeWrappers.add(defaultStatement);
        }
        return astNodeWrappers;
    }

    @Override
    public SwitchStatementWrapper clone() {
        return new SwitchStatementWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public String getInputText() {
        return this.getExpression().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof SwitchStatementWrapper
                && !AstTreeTableValueUtil.compareAstNode(((SwitchStatementWrapper) input).getExpression(),
                this.getExpression())) {
            this.setExpression(((SwitchStatementWrapper) input).getExpression());
            return true;
        }
        return false;
    }
}
