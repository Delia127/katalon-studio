package com.kms.katalon.composer.testcase.ast.treetable;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public interface AstTreeTableNode {
	public ASTNode getASTObject();
	public ASTNode getParentASTObject();
	public boolean isItemEditable();
	public boolean isTestObjectEditable();
	public boolean isInputEditable();
	public boolean isOutputEditatble();
	public String getItemText();
	public String getTestObjectText();
	public String getInputText();
	public String getOutputText();
	public Object getItem();
	public Object getTestObject();
	public Object getInput();
	public Object getOutput();
	public boolean setItem(Object item);
	public boolean setTestObject(Object object);
	public boolean setInput(Object input);
	public boolean setOutput(Object output);
	public CellEditor getCellEditorForItem(Composite parent);
	public CellEditor getCellEditorForTestObject(Composite parent);
	public CellEditor getCellEditorForInput(Composite parent);
	public CellEditor getCellEditorForOutput(Composite parent);
	public AstTreeTableNode getParent();
	public Image getNodeIcon();
	public AstTreeTableNode clone();
	public boolean hasChildren();
	public List<AstTreeTableNode> getChildren() throws Exception;
	public void addChildObject(ASTNode astObject, int index);
	public void removeChildObject(ASTNode astObject);
	public int getChildObjectIndex(ASTNode astObject);
	public String getIndex();
}
