package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

// Base class for all statement
public abstract class StatementWrapper extends ASTNodeWrapper {
    private static final String TEXT = "Statement";
    protected String description = "";

    public StatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public StatementWrapper(StatementWrapper statementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(statementWrapper, parentNodeWrapper);
        this.description = statementWrapper.getDescription();
    }

    public StatementWrapper(Statement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return Collections.emptyList();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getText() {
        return TEXT;
    }

    @Override
    public abstract StatementWrapper clone();

    public StatementWrapper copy(ASTNodeWrapper newParent) {
        StatementWrapper newInstance = clone();
        newInstance.setParent(newParent);
        return newInstance;
    }

    // By default, it's immutable
    public ASTNodeWrapper getInput() {
        return null;
    }
    public String getInputText() {
        return "";
    }

    public boolean updateInputFrom(ASTNodeWrapper input) {
        return false;
    }
}
