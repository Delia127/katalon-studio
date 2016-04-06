package com.kms.katalon.composer.testcase.groovy.ast.statements;

import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import org.codehaus.groovy.ast.stmt.Statement;

/**
 * Created by taittle on 3/24/16.
 */
public abstract class CompositeStatementWrapper extends StatementWrapper implements ASTHasBlock {
    public CompositeStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public CompositeStatementWrapper(StatementWrapper statementWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(statementWrapper, parentNodeWrapper);
    }

    public CompositeStatementWrapper(Statement statement, ASTNodeWrapper parentNodeWrapper) {
        super(statement, parentNodeWrapper);
    }
}
