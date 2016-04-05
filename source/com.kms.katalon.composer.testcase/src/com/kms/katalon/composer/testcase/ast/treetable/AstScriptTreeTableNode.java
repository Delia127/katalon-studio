package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;

public class AstScriptTreeTableNode extends AstAbstractTreeTableNode {
    private ScriptNodeWrapper scriptNode;
    private List<AstTreeTableNode> childNodes = new ArrayList<AstTreeTableNode>();

    public AstScriptTreeTableNode(ScriptNodeWrapper scriptNode, AstTreeTableNode parentNode) {
        super(parentNode);
        this.scriptNode = scriptNode;
        reloadChildren();
    }
    
    @Override
    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public boolean hasChildren() {
        return !childNodes.isEmpty();
    }

    @Override
    public List<AstTreeTableNode> getChildren() {
        return childNodes;
    }

    @Override
    public void reloadChildren() {
        childNodes.clear();
        childNodes.addAll(WrapperToAstTreeConverter.getInstance().convert(scriptNode.getBlock().getStatements(), this));
        for (MethodNodeWrapper method : scriptNode.getMethods()) {
            childNodes.add(new AstMethodTreeTableNode(method, this));
        }
    }

    @Override
    public ScriptNodeWrapper getASTObject() {
        return scriptNode;
    }

    @Override
    public String getItemText() {
        return "";
    }

    @Override
    public Image getIcon() {
        return null;
    }
}
