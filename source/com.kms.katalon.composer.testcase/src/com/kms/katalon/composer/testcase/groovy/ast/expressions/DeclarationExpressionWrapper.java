package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.codehaus.groovy.ast.expr.DeclarationExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class DeclarationExpressionWrapper extends BinaryExpressionWrapper { 
    public DeclarationExpressionWrapper(DeclarationExpression declarationExpression, ASTNodeWrapper parentNodeWrapper) {
        super(declarationExpression, parentNodeWrapper);
    }
}
