package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.PackageNode;

public class PackageNodeWrapper extends AnnonatedNodeWrapper {
    private String name;
    
    public PackageNodeWrapper(PackageNodeWrapper packageNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(packageNodeWrapper, parentNodeWrapper);
        this.name = packageNodeWrapper.getName();
    }
    
    public PackageNodeWrapper(PackageNode packageNode, ASTNodeWrapper parentNodeWrapper) {
        super(packageNode, parentNodeWrapper);
        this.name = packageNode.getName();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public boolean hasAstChildren() {
        return false;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return Collections.emptyList();
    }
}
