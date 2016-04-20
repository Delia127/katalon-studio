package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

/**
 * Represent a Statement that has multiple {@link ComplexChildStatementWrapper} statements and one
 * {@link ComplexLastStatementWrapper} as a last child (that can be null)
 */
public abstract class ComplexStatementWrapper<T extends ComplexChildStatementWrapper, U extends ComplexLastStatementWrapper>
        extends StatementWrapper {
    protected List<T> complexChildStatements = new ArrayList<T>();

    protected U lastStatement = null;

    public ComplexStatementWrapper(StatementWrapper statementWrapper, ExpressionWrapper expression,
            BlockStatementWrapper block, ASTNodeWrapper parentNodeWrapper) {
        super(statementWrapper, parentNodeWrapper);
    }

    public ComplexStatementWrapper(Statement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
    }

    public ComplexStatementWrapper(ComplexStatementWrapper<T, U> complexStatementWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(complexStatementWrapper, parentNodeWrapper);
        for (T complexChildStatement : complexStatementWrapper.getComplexChildStatements()) {
            complexChildStatements.add(copyChild(complexChildStatement));
        }
        if (complexStatementWrapper.hasLastStatement()) {
            lastStatement = copyChild(complexStatementWrapper.getLastStatement());
        }
    }

    public ComplexStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ComplexStatementWrapper() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public T copyChild(T child) {
        return (T) child.copy(this);
    }

    @SuppressWarnings("unchecked")
    public U copyChild(U child) {
        return (U) child.copy(this);
    }

    public List<T> getComplexChildStatements() {
        return Collections.unmodifiableList(complexChildStatements);
    }

    public void addComplexChildStatement(T complexChildStatement) {
        if (complexChildStatement == null) {
            return;
        }
        complexChildStatement.setParent(this);
        complexChildStatements.add(complexChildStatement);
    }

    public boolean addComplexChildStatement(T complexChildStatement, int index) {
        if (complexChildStatement == null || index < 0 || index > complexChildStatements.size()) {
            return false;
        }
        complexChildStatement.setParent(this);
        complexChildStatements.add(index, complexChildStatement);
        return true;
    }

    public boolean removeComplexChildStatement(int index) {
        if (index < 0 || index >= complexChildStatements.size()) {
            return false;
        }
        complexChildStatements.remove(index);
        return true;
    }

    public boolean removeComplexChildStatement(T complexChildStatement) {
        return complexChildStatements.remove(complexChildStatement);
    }

    public int indexOf(T complexChildStatement) {
        return complexChildStatements.indexOf(complexChildStatement);
    }

    public boolean hasLastStatement() {
        return lastStatement != null;
    }

    public U getLastStatement() {
        return lastStatement;
    }

    public boolean setLastStatement(U lastStatement) {
        if (lastStatement == null || lastStatement.isEqualsTo(this.lastStatement)) {
            return false;
        }
        lastStatement.setParent(this);
        this.lastStatement = lastStatement;
        return true;
    }

    public boolean removeLastStatement() {
        if (lastStatement == null) {
            return false;
        }
        lastStatement = null;
        return true;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(complexChildStatements);
        if (hasLastStatement()) {
            astNodeWrappers.add(lastStatement);
        }
        return astNodeWrappers;
    }
}
