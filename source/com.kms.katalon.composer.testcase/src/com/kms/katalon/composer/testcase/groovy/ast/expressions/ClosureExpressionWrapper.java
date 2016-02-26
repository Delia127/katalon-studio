package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapHelper;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class ClosureExpressionWrapper extends ExpressionWrapper implements ASTHasBlock {
    private ParameterWrapper[] parameters;
    private BlockStatementWrapper code;

    public ClosureExpressionWrapper(ParameterWrapper[] parameters, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.parameters = parameters;
        this.code = new BlockStatementWrapper(this);
    }

    public ClosureExpressionWrapper(ClosureExpression closureExpression, ASTNodeWrapper parentNodeWrapper) {
        super(closureExpression, parentNodeWrapper);
        parameters = new ParameterWrapper[closureExpression.getParameters().length];
        for (int i = 0; i < closureExpression.getParameters().length; i++) {
            parameters[i] = new ParameterWrapper(closureExpression.getParameters()[i], this);
        }
        this.code = new BlockStatementWrapper((BlockStatement) closureExpression.getCode(), this);
    }

    public ClosureExpressionWrapper(ClosureExpressionWrapper closureExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(closureExpressionWrapper, parentNodeWrapper);
        parameters = new ParameterWrapper[closureExpressionWrapper.getParameters().length];
        for (int i = 0; i < closureExpressionWrapper.getParameters().length; i++) {
            parameters[i] = new ParameterWrapper(closureExpressionWrapper.getParameters()[i], this);
        }
        this.code = new BlockStatementWrapper(closureExpressionWrapper.getBlock(), this);
    }

    @Override
    public String getText() {
        String paramText = ASTNodeWrapHelper.getParametersText(parameters);
        if (paramText.length() > 0) {
            return "{ " + paramText + " -> ... }";
        } else {
            return "{ -> ... }";
        }
    }

    public ParameterWrapper[] getParameters() {
        return parameters;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        for (ParameterWrapper parameter : parameters) {
            astNodeWrappers.add(parameter);
        }
        astNodeWrappers.add(code);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    @Override
    public ClosureExpressionWrapper clone() {
        return new ClosureExpressionWrapper(this, getParent());
    }
}
