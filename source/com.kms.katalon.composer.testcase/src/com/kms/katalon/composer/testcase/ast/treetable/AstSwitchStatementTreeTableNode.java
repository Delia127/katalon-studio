package com.kms.katalon.composer.testcase.ast.treetable;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;

public class AstSwitchStatementTreeTableNode extends AstCompositeEditableInputStatementTreeTableNode {
    public AstSwitchStatementTreeTableNode(SwitchStatementWrapper statement, AstTreeTableNode parentNode) {
        super(statement, parentNode, StringConstants.TREE_SWITCH_STATEMENT);
    }

    @Override
    public void reloadChildren() {
        SwitchStatementWrapper switchStatement = (SwitchStatementWrapper) statement;
        childNodes.clear();
        childNodes.addAll(WrapperToAstTreeConverter.getInstance().convert(switchStatement.getCaseStatements(), this));
        if (switchStatement.getDefaultStatement() != null) {
            childNodes.add(new AstDefaultStatementTreeTableNode(switchStatement.getDefaultStatement(), this));
        }
    }
}
