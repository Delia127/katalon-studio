package com.kms.katalon.composer.testcase.support;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.treetable.AstTreeTableNode;

public class ItemColumnEditingSupport extends EditingSupport {
	private TreeViewer treeViewer;
	private TestCasePart parentTestCasePart;

	public ItemColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
		super(viewer);
		this.treeViewer = viewer;
		this.parentTestCasePart = parentTestCasePart;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getCellEditorForItem(treeViewer.getTree());
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).isItemEditable();
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getItem();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof AstTreeTableNode) {
			if (element instanceof AstMethodTreeTableNode && value instanceof MethodNode) {
				setValueForMethodNode((AstMethodTreeTableNode) element, (MethodNode) value);
				return;
			}
			if (((AstTreeTableNode) element).setItem(value)) {
				parentTestCasePart.getTreeTableInput().setDirty(true);
				treeViewer.refresh(element);
			}
		}
	}

	protected void setValueForMethodNode(AstMethodTreeTableNode methodNode, MethodNode newMethod) {
		if (methodNode.getASTObject() instanceof MethodNode) {
			try {
				parentTestCasePart.getTreeTableInput().updateMethod((MethodNode) methodNode.getASTObject(), newMethod);
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		}
		return;
	}

}
