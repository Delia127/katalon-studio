package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.composer.testcase.util.WrapperToAstTreeConverter;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.editors.MethodObjectBuilderCellEditor;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;

public class AstMethodTreeTableNode extends AstAbstractTreeTableNode implements IAstItemEditableNode {
    private MethodNodeWrapper methodNode;

    private List<AstTreeTableNode> childNodes = new ArrayList<AstTreeTableNode>();

    public AstMethodTreeTableNode(MethodNodeWrapper methodNode, AstTreeTableNode parentNode) {
        super(parentNode);
        this.methodNode = methodNode;
        reloadChildren();
    }

    @Override
    public MethodNodeWrapper getASTObject() {
        return methodNode;
    }

    @Override
    public String getItemText() {
        return methodNode.getText();
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
    public void reloadChildren() {
        childNodes.clear();
        childNodes.addAll(WrapperToAstTreeConverter.getInstance().convert(methodNode.getBlock().getStatements(), this));
    }

    @Override
    public Image getIcon() {
        return ImageConstants.IMG_16_FUNCTION;
    }

    @Override
    public boolean canEditItem() {
        return true;
    }

    @Override
    public Object getItem() {
        return methodNode;
    }

    @Override
    public CellEditor getCellEditorForItem(Composite parent) {
        return new MethodObjectBuilderCellEditor(parent, methodNode.getText(), methodNode.getParent());
    }

    @Override
    public boolean setItem(Object item) {
        if (!(item instanceof MethodNodeWrapper)) {
            return false;
        }
        MethodNodeWrapper newMethodNode = (MethodNodeWrapper) item;
        methodNode.setName(newMethodNode.getName());
        methodNode.setModifiers(newMethodNode.getModifiers());
        methodNode.setParameters(newMethodNode.getParameters());
        methodNode.setReturnType(newMethodNode.getReturnType());
        methodNode.setExceptions(newMethodNode.getExceptions());
        methodNode.setAnnotations(newMethodNode.getAnnotations());
        return true;
    }

    @Override
    public List<AstTreeTableNode> getChildren() {
        return childNodes;
    }
    
    @Override
    public boolean isChildAssignble(ASTNodeWrapper astNode) {
        return methodNode.isChildAssignble(astNode);
    }

    public boolean addChild(ASTNodeWrapper childObject) {
        return methodNode.addChild(childObject);
    }

    public boolean addChild(ASTNodeWrapper childObject, int index) {
        return methodNode.addChild(childObject, index);
    }

    public boolean removeChild(ASTNodeWrapper childObject) {
        return methodNode.removeChild(childObject);
    }

    public int indexOf(ASTNodeWrapper childObject) {
        return methodNode.indexOf(childObject);
    }
}
