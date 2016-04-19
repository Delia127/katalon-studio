package com.kms.katalon.composer.testcase.groovy.ast.statements;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public abstract class ComplexChildStatementWrapper extends CompositeStatementWrapper {
    public ComplexChildStatementWrapper() {
        this(null);
    }
    
    public ComplexChildStatementWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ComplexChildStatementWrapper(Statement statement, BlockStatement block, ASTNodeWrapper parentNodeWrapper) {
        super(statement, block, parentNodeWrapper);
    }

    public ComplexChildStatementWrapper(ComplexChildStatementWrapper complexLastStatementWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(complexLastStatementWrapper, parentNodeWrapper);
    }

    @Override
    public boolean canHaveDescription() {
        return false;
    }

    @Override
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return (isAstNodeBelongToParentComplex(astNode) || super.isChildAssignble(astNode));
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        if (isAstNodeBelongToParentComplex(childObject)) {
            ASTNodeWrapper parent = getParent();
            return getParent().addChild(childObject, parent.indexOf(this) + 1);
        }
        return super.addChild(childObject);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        if (isAstNodeBelongToParentComplex(childObject)) {
            return getParent().addChild(childObject, index);
        }
        return super.addChild(childObject, index);
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        if (isAstNodeBelongToParentComplex(childObject)) {
            return getParent().removeChild(childObject);
        }
        return super.removeChild(childObject);
    }

    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        if (isAstNodeBelongToParentComplex(childObject)) {
            return getParent().indexOf(childObject);
        }
        return super.indexOf(childObject);
    }

    protected abstract boolean isAstNodeBelongToParentComplex(ASTNodeWrapper astNode);
}
