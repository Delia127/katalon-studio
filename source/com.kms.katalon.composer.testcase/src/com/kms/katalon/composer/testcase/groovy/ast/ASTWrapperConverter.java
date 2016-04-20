package com.kms.katalon.composer.testcase.groovy.ast;

import org.codehaus.groovy.ast.ASTNode;

interface ASTWrapperConverter<T extends ASTNode> {
    public ASTNodeWrapper wrap(T node, ASTNodeWrapper parentNode);
}