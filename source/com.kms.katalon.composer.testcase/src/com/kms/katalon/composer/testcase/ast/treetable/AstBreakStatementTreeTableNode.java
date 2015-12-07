package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.BreakStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;

public class AstBreakStatementTreeTableNode extends AstStatementTreeTableNode {

    public AstBreakStatementTreeTableNode(BreakStatement statement, AstTreeTableNode parentNode, ASTNode parentObject,
            ClassNode scriptClass) {
        super(statement, parentNode, parentObject, scriptClass);
    }

    @Override
    public String getItemText() {
        return StringConstants.TREE_BREAK_STATEMENT;
    }
}
