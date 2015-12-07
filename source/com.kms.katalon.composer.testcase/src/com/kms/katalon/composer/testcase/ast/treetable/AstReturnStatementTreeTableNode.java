package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ReturnStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;

public class AstReturnStatementTreeTableNode extends AstStatementTreeTableNode {

    public AstReturnStatementTreeTableNode(ReturnStatement statement, AstTreeTableNode parentNode, ASTNode parentObject,
            ClassNode scriptClass) {
        super(statement, parentNode, parentObject, scriptClass);
    }

    @Override
    public String getItemText() {
        return StringConstants.TREE_RETURN_STATEMENT;
    }
}
