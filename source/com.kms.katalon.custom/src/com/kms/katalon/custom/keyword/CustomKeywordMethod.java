package com.kms.katalon.custom.keyword;

import org.codehaus.groovy.ast.MethodNode;

public class CustomKeywordMethod {
    
    private MethodNode methodNode;
    
    private String javadoc;
    
    public CustomKeywordMethod(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    public MethodNode getMethodNode() {
        return methodNode;
    }

    public void setMethodNode(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }
}
