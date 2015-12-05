package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.editors.MethodObjectBuilderCellEditor;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableUtil;
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.core.ast.GroovyParser;

public class AstMethodTreeTableNode extends AstAbstractTreeTableNode {
	private MethodNode methodNode;
	private AstTreeTableNode parentNode;
	private ClassNode parentClass;

	public AstMethodTreeTableNode(MethodNode methodNode, AstTreeTableNode parentNode, ClassNode parentClass) {
		this.methodNode = methodNode;
		this.parentNode = parentNode;
		this.parentClass = parentClass;
	}

	@Override
	public ASTNode getASTObject() {
		return methodNode;
	}

	@Override
	public ASTNode getParentASTObject() {
		return parentClass;
	}

	@Override
	public String getItemText() {
		return methodNode.getName() + "()";
	}

	@Override
	public boolean hasChildren() {
		if (methodNode.getCode() != null) {
			if (methodNode.getCode() instanceof BlockStatement) {
				return true;
			} else if (methodNode.getCode() instanceof ReturnStatement) {
				ReturnStatement returnStatement = (ReturnStatement) methodNode.getCode();
				if (returnStatement.getExpression() instanceof ConstantExpression
						&& ((ConstantExpression) returnStatement.getExpression()).getValue() == null) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return AstTreeTableUtil.getChildren(methodNode, this, parentClass);
	}

	@Override
	public AstTreeTableNode getParent() {
		return parentNode;
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		AstTreeTableUtil.addChild(methodNode.getCode(), astObject, index);
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		AstTreeTableUtil.removeChild(methodNode.getCode(), astObject);
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return AstTreeTableUtil.getIndex(methodNode.getCode(), astObject);
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_FUNCTION;
	}

	@Override
	public AstTreeTableNode clone() {
		return new AstMethodTreeTableNode(GroovyParser.cloneMethodNode(methodNode), parentNode, parentClass);
	}

	@Override
	public boolean isItemEditable() {
		return true;
	}
	
	@Override
	public Object getItem() {
		return methodNode;
	}

	@Override
	public CellEditor getCellEditorForItem(Composite parent) {
		return new MethodObjectBuilderCellEditor(parent, AstTextValueUtil.getTextValue(methodNode), parentClass);
	}

	@Override
	public boolean setItem(Object item) {
		if (item instanceof MethodNode) {
			MethodNode newMethodNode = (MethodNode) item;
			methodNode.setParameters(newMethodNode.getParameters());
			methodNode.setReturnType(newMethodNode.getReturnType());
		}
		return true;
	}
}
