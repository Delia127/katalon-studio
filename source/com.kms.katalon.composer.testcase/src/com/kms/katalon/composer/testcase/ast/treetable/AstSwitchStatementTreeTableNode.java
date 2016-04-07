package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;

public class AstSwitchStatementTreeTableNode extends AstCompositeInputEditableStatementTreeTableNode {
    public AstSwitchStatementTreeTableNode(SwitchStatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode, StringConstants.TREE_SWITCH_STATEMENT);
    }

    @Override
    public void reloadChildren() {
        SwitchStatementWrapper switchStatement = (SwitchStatementWrapper) statement;
        childNodes.clear();
        for (CaseStatementWrapper caseStatement : switchStatement.getComplexChildStatements()) {
            childNodes.add(new AstCompositeInputEditableStatementTreeTableNode(caseStatement, this,
                    StringConstants.TREE_CASE_STATEMENT));
        }
        if (switchStatement.hasLastStatement()) {
            childNodes.add(new AstCompositeStatementTreeTableNode(switchStatement.getLastStatement(), this,
                    StringConstants.TREE_DEFAULT_STATEMENT));
        }
    }
}
