package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class ForStatementWrapper extends CompositeStatementWrapper {
    private static final Class<?> DEFAULT_TYPE = Object.class;
    private static final String DEFAULT_VARIABLE_NAME = "index";
    private ParameterWrapper variable;
    private ExpressionWrapper collectionExpression;
    private BlockStatementWrapper loopBlock;

    public ForStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        variable = new ParameterWrapper(DEFAULT_TYPE, DEFAULT_VARIABLE_NAME, this);
        collectionExpression = new RangeExpressionWrapper(this);
        loopBlock = new BlockStatementWrapper(this);
    }

    public ForStatementWrapper(ForStatement forStatement, ASTNodeWrapper parentNodeWrapper) {
        super(forStatement, parentNodeWrapper);
        variable = new ParameterWrapper(forStatement.getVariable(), this);
        collectionExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(forStatement.getCollectionExpression(),
                this);
        loopBlock = new BlockStatementWrapper((BlockStatement) forStatement.getLoopBlock(), parentNodeWrapper);
    }

    public ForStatementWrapper(ForStatementWrapper forStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(forStatementWrapper, parentNodeWrapper);
        variable = new ParameterWrapper(forStatementWrapper.getVariable(), this);
        collectionExpression = forStatementWrapper.getCollectionExpression().copy(this);
        loopBlock = new BlockStatementWrapper(forStatementWrapper.getBlock(), parentNodeWrapper);
    }

    public ParameterWrapper getVariable() {
        return variable;
    }

    public void setVariable(ParameterWrapper variable) {
        this.variable = variable;
    }

    public ExpressionWrapper getCollectionExpression() {
        return collectionExpression;
    }

    public void setCollectionExpression(ExpressionWrapper collectionExpression) {
        this.collectionExpression = collectionExpression;
    }

    @Override
    public String getInputText() {
        String value = "";
        if (!(getCollectionExpression() instanceof ClosureListExpressionWrapper)) {
            if (!isForLoopDummy(getVariable())) {
                value += getVariable().getText();
                value += " : ";
            }
        }
        return value + getCollectionExpression().getText();
    }

    @Override
    public String getText() {
        return "For (" + getInputText() + ")";
    }

    public static boolean isForLoopDummy(ParameterWrapper variable) {
        return variable.getType().getName().equals(ForStatement.FOR_LOOP_DUMMY.getType().getName())
                && variable.getName().equals(ForStatement.FOR_LOOP_DUMMY.getName());
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(variable);
        astNodeWrappers.add(collectionExpression);
        astNodeWrappers.add(loopBlock);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return loopBlock;
    }

    @Override
    public ForStatementWrapper clone() {
        return new ForStatementWrapper(this, getParent());
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof ForStatementWrapper)
                || (AstTreeTableValueUtil.compareAstNode(this.getVariable(), ((ForStatementWrapper) input).getVariable())
                && AstTreeTableValueUtil.compareAstNode(this.getCollectionExpression(), ((ForStatementWrapper) input).getCollectionExpression()))) {
            return false;
        }
        ForStatementWrapper newForStatement = (ForStatementWrapper) input;
        ParameterWrapper variable = newForStatement.getVariable();
        variable.setParent(this);
        this.setVariable(variable);
        ExpressionWrapper collectionExpression = newForStatement.getCollectionExpression();
        collectionExpression.setParent(this);
        this.setCollectionExpression(collectionExpression);
        return true;
    }
}
