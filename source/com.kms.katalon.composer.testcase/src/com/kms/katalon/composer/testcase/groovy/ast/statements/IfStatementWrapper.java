package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;

public class IfStatementWrapper extends ComplexStatementWrapper<ElseIfStatementWrapper, ElseStatementWrapper> implements
        ASTHasBlock {
    private BlockStatementWrapper block = new BlockStatementWrapper(this);

    private BooleanExpressionWrapper expression;
    
    public IfStatementWrapper() {
        this(null);
    }

    public IfStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(this);
    }

    public IfStatementWrapper(BooleanExpressionWrapper booleanExpression, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(booleanExpression, this);
    }

    public IfStatementWrapper(IfStatement ifStatement, ASTNodeWrapper parentNodeWrapper) {
        super(ifStatement, parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(ifStatement.getBooleanExpression(), this);
        this.block = new BlockStatementWrapper(initIfBlock(ifStatement), this);
        getStatementNodeWrappersFromIfStatement(ifStatement.getElseBlock());
    }
    
    private static BlockStatement initIfBlock(IfStatement ifStatement) {
        Statement ifBlock = ifStatement.getIfBlock();
        if (ifBlock instanceof BlockStatement) {
            return (BlockStatement) ifBlock;
        }
        BlockStatement block = new BlockStatement();
        block.addStatement(ifBlock);
        return block;
    }

    public IfStatementWrapper(IfStatementWrapper ifStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(ifStatementWrapper, parentNodeWrapper);
        this.expression = new BooleanExpressionWrapper(ifStatementWrapper.getBooleanExpression(), this);
        this.block = new BlockStatementWrapper(ifStatementWrapper.getBlock(), this);
    }

    private void getStatementNodeWrappersFromIfStatement(Statement statement) {
        if (statement instanceof EmptyStatement) {
            return;
        }
        if (statement instanceof IfStatement) {
            IfStatement elseIfStatement = (IfStatement) statement;
            addComplexChildStatement(new ElseIfStatementWrapper(elseIfStatement, this));
            getStatementNodeWrappersFromIfStatement(elseIfStatement.getElseBlock());
            return;
        }
        if (statement instanceof BlockStatement) {
            setLastStatement(new ElseStatementWrapper((BlockStatement) statement, this));
            return;
        }
        BlockStatement block = new BlockStatement();
        block.addStatement(statement);
        setLastStatement(new ElseStatementWrapper(block, this));
    }

    public BooleanExpressionWrapper getBooleanExpression() {
        return (BooleanExpressionWrapper) expression;
    }

    public void setBooleanExpression(BooleanExpressionWrapper booleanExpression) {
        if (booleanExpression == null) {
            return;
        }
        booleanExpression.setParent(this);
        this.expression = booleanExpression;
    }

    @Override
    public String getText() {
        return "if (" + getInputText() + ")";
    }

    @Override
    public IfStatementWrapper clone() {
        return new IfStatementWrapper(this, getParent());
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return block;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(expression);
        astNodeWrappers.add(block);
        return astNodeWrappers;
    }

    @Override
    public BooleanExpressionWrapper getInput() {
        return getBooleanExpression();
    }

    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public String getInputText() {
        return getBooleanExpression().getText();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (input instanceof BooleanExpressionWrapper && !getBooleanExpression().isEqualsTo(input)) {
            setBooleanExpression((BooleanExpressionWrapper) input);
            return true;
        }
        return false;
    }

    @Override
    public boolean isChildAssignble(ASTNodeWrapper nodeWrapper) {
        return (nodeWrapper instanceof ElseIfStatementWrapper || nodeWrapper instanceof ElseStatementWrapper || getBlock().isChildAssignble(nodeWrapper));
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        if (childObject instanceof ElseIfStatementWrapper) {
            addComplexChildStatement((ElseIfStatementWrapper) childObject);
            return true;
        }
        if (childObject instanceof ElseStatementWrapper) {
            return setLastStatement((ElseStatementWrapper) childObject);
        }
        return getBlock().addChild(childObject);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        if (childObject instanceof ElseIfStatementWrapper) {
            return addComplexChildStatement((ElseIfStatementWrapper) childObject, index);
        }
        if (childObject instanceof ElseStatementWrapper) {
            return setLastStatement((ElseStatementWrapper) childObject);
        }
        return getBlock().addChild(childObject, index);
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        if (childObject instanceof ElseIfStatementWrapper) {
            return removeComplexChildStatement((ElseIfStatementWrapper) childObject);
        } else if (childObject == lastStatement) {
            return removeLastStatement();
        }
        return getBlock().removeChild(childObject);
    }

    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        if (childObject instanceof ElseIfStatementWrapper) {
            return indexOf((ElseIfStatementWrapper) childObject);
        } else if (childObject == lastStatement) {
            return 0;
        }
        return getBlock().indexOf(childObject);
    }
}
