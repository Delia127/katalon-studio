package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
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
            if (ClassNodeWrapper.RUN_METHOD_NAME.equals(method.getName())) {
                continue;
            }
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
    
    @Override
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return scriptNode.getBlock().isChildAssignble(astNode);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject) {
        return scriptNode.getBlock().addChild(childObject);
    }

    @Override
    public boolean addChild(ASTNodeWrapper childObject, int index) {
        return scriptNode.getBlock().addChild(childObject, index);
    }

    @Override
    public boolean removeChild(ASTNodeWrapper childObject) {
        return scriptNode.getBlock().removeChild(childObject);
    }
    
    @Override
    public int indexOf(ASTNodeWrapper childObject) {
        return scriptNode.getBlock().indexOf(childObject);
    }
}
