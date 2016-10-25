package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.CommentWrapper;
import com.kms.katalon.core.constants.StringConstants;

/**
 * Base class for any statement contains list of statements
 *
 */
public class BlockStatementWrapper extends StatementWrapper {
    protected List<StatementWrapper> statements = new ArrayList<StatementWrapper>();

    protected List<CommentWrapper> insideComments = new ArrayList<CommentWrapper>();

    public BlockStatementWrapper() {
        this(null);
    }

    public BlockStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public BlockStatementWrapper(BlockStatementWrapper blockStatementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(blockStatementWrapper, parentNodeWrapper);
        for (StatementWrapper statement : blockStatementWrapper.getStatements()) {
            statements.add(statement.copy(this));
        }
        for (CommentWrapper insideComment : blockStatementWrapper.getInsideComments()) {
            insideComments.add(new CommentWrapper(insideComment, this));
        }
    }

    public BlockStatementWrapper(BlockStatement blockStatement, ASTNodeWrapper parentNodeWrapper) {
        super(blockStatement, parentNodeWrapper);
        statements.addAll(getStatementNodeWrappersFromBlockStatement(blockStatement, this));
        if (statements.size() == 1 && statements.get(0) instanceof ReturnStatementWrapper
                && (((ReturnStatementWrapper) statements.get(0)).getExpression().getText().equals("null"))) {
            statements.clear();
        }
    }

    public BlockStatementWrapper(List<StatementWrapper> statements, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        addStatements(statements);
    }

    public List<StatementWrapper> getStatements() {
        return Collections.unmodifiableList(statements);
    }

    public void addStatement(StatementWrapper statement) {
        if (statement == null) {
            return;
        }
        statement.setParent(this);
        statements.add(statement);
    }

    public boolean addStatement(StatementWrapper statement, int index) {
        if (statement == null || index < 0 || index > statements.size()) {
            return false;
        }
        statement.setParent(this);
        statements.add(index, statement);
        return true;
    }

    public void addStatements(List<StatementWrapper> listOfStatements) {
        if (listOfStatements == null) {
            return;
        }
        for (StatementWrapper statement : listOfStatements) {
            if (statement == null) {
                continue;
            }
            statement.setParent(this);
            statements.add(statement);
        }
    }

    public boolean removeStatement(StatementWrapper statement) {
        return statements.remove(statement);
    }

    public boolean removeChild(int index) {
        if (index < 0 || index >= statements.size()) {
            return false;
        }
        statements.remove(index);
        return true;
    }

    public int indexOf(StatementWrapper childStatement) {
        if (childStatement == null) {
            return -1;
        }
        return statements.indexOf(childStatement);
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(statements);
        return astNodeWrappers;
    }

    public List<CommentWrapper> getInsideComments() {
        return insideComments;
    }

    @Override
    public void setInsideComments(List<CommentWrapper> commentWrapperList) {
        if (!statements.isEmpty()) {
            super.setInsideComments(commentWrapperList);
            return;
        }
        if (commentWrapperList == null || commentWrapperList.isEmpty()) {
            return;
        }
        insideComments.addAll(commentWrapperList);
    }

    @Override
    public BlockStatementWrapper clone() {
        return new BlockStatementWrapper(this, getParent());
    }

    @Override
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return (astNode instanceof StatementWrapper && !(astNode instanceof ComplexChildStatementWrapper) && !(astNode instanceof ComplexLastStatementWrapper));
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        if (childObject instanceof StatementWrapper) {
            addStatement((StatementWrapper) childObject);
            return true;
        }
        return false;
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        if (childObject instanceof StatementWrapper) {
            return addStatement((StatementWrapper) childObject, index);
        }
        return false;
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        if (childObject instanceof StatementWrapper) {
            return removeStatement((StatementWrapper) childObject);
        }
        return false;
    }

    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        if (childObject instanceof StatementWrapper) {
            return indexOf((StatementWrapper) childObject);
        }
        return -1;
    }

    private static boolean isDescriptionStatement(Statement statement) {
        return (statement instanceof ExpressionStatement
                && ((ExpressionStatement) statement).getExpression() instanceof ConstantExpression && ((ConstantExpression) ((ExpressionStatement) statement).getExpression()).getValue() instanceof String);
    }

    private static String getDecriptionStatementValue(Statement statement) {
        return ((ConstantExpression) ((ExpressionStatement) statement).getExpression()).getValue().toString();
    }

    public static List<StatementWrapper> getStatementNodeWrappersFromBlockStatement(BlockStatement blockStatement,
            ASTNodeWrapper parentNode) {
        List<StatementWrapper> statements = new ArrayList<StatementWrapper>();
        Statement pendingDescriptionStatement = null;
        List<Statement> statementList = blockStatement.getStatements();
        int statementsNumber = statementList.size();
        for (int index = 0; index < statementsNumber; index++) {
            Statement statement = statementList.get(index);
            if (index < statementsNumber && isDescriptionStatement(statement)) {
                if (pendingDescriptionStatement != null) {
                    statements.add(ASTNodeWrapHelper.getStatementNodeWrapperFromStatement(pendingDescriptionStatement,
                            parentNode));
                }
                pendingDescriptionStatement = statement;
            } else {
                StatementWrapper statementWrapper = ASTNodeWrapHelper.getStatementNodeWrapperFromStatement(statement,
                        parentNode);
                if (statementWrapper == null) {
                    continue;
                }
                if (pendingDescriptionStatement != null) {
                    statementWrapper.setDescription(getDecriptionStatementValue(pendingDescriptionStatement));
                    if (StringUtils.equals(StringConstants.NOT_RUN_LABEL,
                            pendingDescriptionStatement.getStatementLabel())) {
                        statementWrapper.disable();
                    }
                    pendingDescriptionStatement = null;
                }
                statements.add(statementWrapper);
            }
        }
        if (pendingDescriptionStatement != null) {
            statements.add(ASTNodeWrapHelper.getStatementNodeWrapperFromStatement(pendingDescriptionStatement,
                    parentNode));
        }
        return statements;
    }
    
    @Override 
    public boolean canHaveLabel() { 
        return false; 
    }
}
