package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;

public class AstElseIfStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstElseIfStatementTreeTableNode(ElseIfStatementWrapper elseIfStatement, AstTreeTableNode parentNode) {
        super(elseIfStatement, parentNode, ImageConstants.IMG_16_ELSE_IF, StringConstants.TREE_ELSE_IF_STATEMENT);
    }
    
    @Override
    public ElseIfStatementWrapper getASTObject() {
        return (ElseIfStatementWrapper) statement;
    }
}
