package com.kms.katalon.composer.testcase.ast.treetable;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ThrowStatement;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;

public class AstThrowStatementTreeTableNode extends AstStatementTreeTableNode {

    public AstThrowStatementTreeTableNode(ThrowStatement throwStatement, AstTreeTableNode parentNode, ASTNode parentObject,
            ClassNode scriptClass) {
        super(throwStatement, parentNode, parentObject, scriptClass);
    }

    @Override
    public String getItemText() {
        return StringConstants.TREE_THROW_STATEMENT;
    }
    
    @Override
    public String getInputText() {
        return AstTreeTableTextValueUtil.getInstance().getTextValue(((ThrowStatement) statement).getExpression());
    }
}
