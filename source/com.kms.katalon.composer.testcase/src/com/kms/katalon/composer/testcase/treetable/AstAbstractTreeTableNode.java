package com.kms.katalon.composer.testcase.treetable;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public abstract class AstAbstractTreeTableNode implements AstTreeTableNode {
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AstTreeTableNode)) {
            return false;
        }
        AstTreeTableNode that = (AstTreeTableNode) object;
        return new EqualsBuilder().append(this.getASTObject(), that.getASTObject()).isEquals();
    }

	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 31).appendSuper(super.hashCode()).append(this.getASTObject().hashCode())
				.toHashCode();
	}

	@Override
	public boolean isItemEditable() {
		return false;
	}

	@Override
	public boolean isTestObjectEditable() {
		return false;
	}

	@Override
	public boolean isInputEditable() {
		return false;
	}

	@Override
	public boolean isOutputEditatble() {
		return false;
	}

	@Override
	public Object getItem() {
		return null;
	}

	@Override
	public Object getTestObject() {
		return null;
	}

	@Override
	public Object getInput() {
		return null;
	}

	@Override
	public Object getOutput() {
		return null;
	}

	@Override
	public String getTestObjectText() {
		return "";
	}

	@Override
	public String getInputText() {
		return "";
	}

	@Override
	public String getOutputText() {
		return "";
	}

	@Override
	public boolean setItem(Object item) {
		return false;
	}

	@Override
	public boolean setTestObject(Object object) {
		return false;
	}

	@Override
	public boolean setInput(Object input) {
		return false;
	}

	@Override
	public boolean setOutput(Object output) {
		return false;
	}

	@Override
	public CellEditor getCellEditorForItem(Composite parent) {
		return null;
	}

	@Override
	public CellEditor getCellEditorForTestObject(Composite parent) {
		return null;
	}

	@Override
	public CellEditor getCellEditorForInput(Composite parent) {
		return null;
	}

	@Override
	public CellEditor getCellEditorForOutput(Composite parent) {
		return null;
	}

	@Override
	public abstract AstTreeTableNode clone();

	@Override
	public List<AstTreeTableNode> getChildren() throws Exception {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public int getChildObjectIndex(ASTNode astObject) {
		return -1;
	}

	@Override
	public void addChildObject(ASTNode astObject, int index) {
		// Do nothing
	}

	@Override
	public void removeChildObject(ASTNode astObject) {
		// Do nothing
	}

	@Override
	public String getIndex() {
		if (getParent() == null) {
			return "";
		}
		String parentIndex = getParent().getIndex();
		try {
			return ((!parentIndex.isEmpty()) ? parentIndex + "." : "") + (getParent().getChildren().indexOf(this) + 1);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return "";
	}
}
