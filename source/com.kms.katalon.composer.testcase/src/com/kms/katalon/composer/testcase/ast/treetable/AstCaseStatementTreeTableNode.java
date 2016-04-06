package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;

public class AstCaseStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstCaseStatementTreeTableNode(CaseStatementWrapper caseStatement, AstTreeTableNode parentNode) {
        super(caseStatement, parentNode, StringConstants.TREE_CASE_STATEMENT);
    }
}
