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

public class SwitchStatementWrapper extends ComplexStatementWrapper<CaseStatementWrapper, DefaultStatementWrapper> {
    private ExpressionWrapper expression;
    
    public SwitchStatementWrapper() {
        this(null);
    }

    public SwitchStatementWrapper(SwitchStatement switchStatement, ASTNodeWrapper parentNodeWrapper) {
        super(switchStatement, parentNodeWrapper);
        this.expression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(switchStatement.getExpression(),
                this);
        for (CaseStatement caseStatement : switchStatement.getCaseStatements()) {
            complexChildStatements.add(new CaseStatementWrapper(caseStatement, this));
        }
        if (switchStatement.getDefaultStatement() instanceof BlockStatement) {
            lastStatement = new DefaultStatementWrapper((BlockStatement) switchStatement.getDefaultStatement(), this);
        }
    }

    public SwitchStatementWrapper(SwitchStatementWrapper switchStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(switchStatementWrapper, parentNodeWrapper);
        this.expression = switchStatementWrapper.getExpression().copy(this);
    }

    public SwitchStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(this);
        lastStatement = new DefaultStatementWrapper(this);
        lastStatement.getBlock().addStatement(new BreakStatementWrapper(lastStatement));
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("switch (");
        stringBuilder.append(getInputText());
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
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public SwitchStatementWrapper clone() {
        return new SwitchStatementWrapper(this, getParent());
    }

    @Override
    public SwitchStatementWrapper getInput() {
        return this;
    }

    @Override
    public String getInputText() {
        return getExpression().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof SwitchStatementWrapper
                && !getExpression().isEqualsTo(((SwitchStatementWrapper) input).getExpression())) {
            setExpression(((SwitchStatementWrapper) input).getExpression());
            return true;
        }
        return false;
    }

    @Override
    public boolean isChildAssignble(ASTNodeWrapper nodeWrapper) {
        return (nodeWrapper instanceof CaseStatementWrapper || nodeWrapper instanceof DefaultStatementWrapper);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        if (childObject instanceof CaseStatementWrapper) {
            addComplexChildStatement((CaseStatementWrapper) childObject);
            return true;
        } else if (childObject instanceof DefaultStatementWrapper) {
            return setLastStatement((DefaultStatementWrapper) childObject);
        }
        return false;
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        if (childObject instanceof CaseStatementWrapper) {
            return addComplexChildStatement((CaseStatementWrapper) childObject, index);
        } else if (childObject instanceof DefaultStatementWrapper) {
            return setLastStatement((DefaultStatementWrapper) childObject);
        }
        return false;
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        if (childObject instanceof CaseStatementWrapper) {
            return removeComplexChildStatement((CaseStatementWrapper) childObject);
        } else if (childObject == lastStatement) {
            return removeLastStatement();
        }
        return false;
    }

    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        if (childObject instanceof CaseStatementWrapper) {
            return indexOf((CaseStatementWrapper) childObject);
        } else if (childObject == lastStatement) {
            return 0;
        }
        return -1;
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
