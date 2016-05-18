package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.core.constants.StringConstants;

// Base class for all statement
public abstract class StatementWrapper extends ASTNodeWrapper {
    public static final String TEXT = "Statement";
    
    public static final String NOT_RUN_LABEL = StringConstants.NOT_RUN_LABEL;

    protected String description = "";
    
    protected String label;

    public StatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public StatementWrapper(StatementWrapper statementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(statementWrapper, parentNodeWrapper);
        this.description = statementWrapper.getDescription();
        this.label = statementWrapper.getLabel();
    }

    public StatementWrapper(Statement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
        this.label = statement.getStatementLabel();
    }

    public boolean canHaveDescription() {
        return true;
    }

    public boolean hasDescription() {
        return !StringUtils.isEmpty(description);
    }

    public String getDescription() {
        return description;
    }

    public boolean setDescription(String description) {
        if (description == null || StringUtils.equals(description, this.description)) {
            return false;
        }
        this.description = description;
        return true;
    }

    @Override
    public String getText() {
        return TEXT;
    }

    @Override
    public abstract StatementWrapper clone();

    @Override
    public StatementWrapper copy(ASTNodeWrapper newParent) {
        return (StatementWrapper) super.copy(newParent);
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return Collections.emptyList();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean canHaveLabel() {
        return true;
    }
    
    public boolean disable() {
        if (!isDisabled()) {
            setLabel(NOT_RUN_LABEL);
            return true;
        }
        return false;
    }
    
    public boolean enable() {
        if (isDisabled()) {
            setLabel(null);
            return true;
        }
        return false;
    }

    public boolean isDisabled() {
        return StringUtils.equals(label, NOT_RUN_LABEL);
    }
}
