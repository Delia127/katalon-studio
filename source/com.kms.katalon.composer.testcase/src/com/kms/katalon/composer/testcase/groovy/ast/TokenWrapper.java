package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.List;

import org.codehaus.groovy.syntax.Token;

public class TokenWrapper extends ASTNodeWrapper {
    private Token token;

    public TokenWrapper(TokenWrapper tokenWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(tokenWrapper, parentNodeWrapper);
        this.token = tokenWrapper.token;
    }

    public TokenWrapper(Token token, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.lineNumber = token.getStartLine();
        this.columnNumber = token.getStartColumn();
        this.lastLineNumber = lineNumber;
        this.lastColumnNumber = columnNumber + token.getText().length();
        this.token = token;
    }

    @Override
    public String getText() {
        return token.getText();
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return null;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int getMeaning() {
        return token.getMeaning();
    }

    public int getType() {
        return token.getType();
    }
}
