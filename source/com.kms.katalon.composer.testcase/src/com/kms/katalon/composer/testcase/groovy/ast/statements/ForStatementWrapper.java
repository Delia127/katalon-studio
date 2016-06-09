package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;

public class ForStatementWrapper extends CompositeStatementWrapper {
    private static final Class<?> DEFAULT_TYPE = Object.class;

    private static final String DEFAULT_VARIABLE_NAME = "index";

    private ParameterWrapper variable;

    private ExpressionWrapper collectionExpression;

    public ForStatementWrapper() {
        this(null);
    }

    public ForStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        variable = new ParameterWrapper(DEFAULT_TYPE, DEFAULT_VARIABLE_NAME, this);
        collectionExpression = new RangeExpressionWrapper(this);
    }

    public ForStatementWrapper(ForStatement forStatement, ASTNodeWrapper parentNodeWrapper) {
        super(forStatement, initLoopBlock(forStatement), parentNodeWrapper);
        variable = new ParameterWrapper(forStatement.getVariable(), this);
        collectionExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                forStatement.getCollectionExpression(), this);
    }

    private static BlockStatement initLoopBlock(ForStatement forStatement) {
        Statement loopBlock = forStatement.getLoopBlock();
        if (loopBlock instanceof BlockStatement) {
            return (BlockStatement) loopBlock;
        }
        BlockStatement block = new BlockStatement();
        block.addStatement(loopBlock);
        return block;
    }

    public ForStatementWrapper(ForStatementWrapper forStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(forStatementWrapper, parentNodeWrapper);
        variable = new ParameterWrapper(forStatementWrapper.getVariable(), this);
        collectionExpression = forStatementWrapper.getCollectionExpression().copy(this);
    }

    public ParameterWrapper getVariable() {
        return variable;
    }

    public void setVariable(ParameterWrapper variable) {
        if (variable == null) {
            return;
        }
        variable.setParent(this);
        this.variable = variable;
    }

    public ExpressionWrapper getCollectionExpression() {
        return collectionExpression;
    }

    public void setCollectionExpression(ExpressionWrapper collectionExpression) {
        if (collectionExpression == null) {
            return;
        }
        collectionExpression.setParent(this);
        this.collectionExpression = collectionExpression;
    }

    @Override
    public String getText() {
        return "for (" + getInputText() + ")";
    }

    // For closure list expression collection type of for statement
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
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public ForStatementWrapper clone() {
        return new ForStatementWrapper(this, getParent());
    }

    @Override
    public boolean isInputEditatble() {
        return true;
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
    public ForStatementWrapper getInput() {
        return this;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof ForStatementWrapper)
                || (getVariable().isEqualsTo(((ForStatementWrapper) input).getVariable()) && getCollectionExpression().isEqualsTo(
                        ((ForStatementWrapper) input).getCollectionExpression()))) {
            return false;
        }
        ForStatementWrapper newForStatement = (ForStatementWrapper) input;
        setVariable(newForStatement.getVariable());
        setCollectionExpression(newForStatement.getCollectionExpression());
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getCollectionExpression() && newChild instanceof ExpressionWrapper) {
            ExpressionWrapper newCollectionExpression = (ExpressionWrapper) newChild;
            ParameterWrapper variable = getVariable();
            if (newCollectionExpression instanceof ClosureListExpressionWrapper) {
                variable = new ParameterWrapper(ForStatement.FOR_LOOP_DUMMY, this);
            } else if (isForLoopDummy(variable)) {
                variable = new ParameterWrapper(Object.class, DEFAULT_VARIABLE_NAME, this);
            }
            variable.copyProperties(getVariable());
            setVariable(variable);
            setCollectionExpression(newCollectionExpression);
            return true;
        } else if (oldChild == getVariable() && newChild instanceof ParameterWrapper) {
            setVariable((ParameterWrapper) newChild);
            return true;
        }
        return super.replaceChild(oldChild, newChild);
    }
}
