package com.kms.katalon.composer.testcase.support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.AstTreeTableNode;

public class OutputColumnEditingSupport extends EditingSupport {

	private TreeViewer treeViewer;
	private TestCasePart parentTestCasePart;

	public OutputColumnEditingSupport(TreeViewer treeViewer, TestCasePart parentTestCasePart) {
		super(treeViewer);
		this.treeViewer = treeViewer;
		this.parentTestCasePart = parentTestCasePart;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getCellEditorForOutput(treeViewer.getTree());
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).isOutputEditatble();
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getOutput();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof AstTreeTableNode && ((AstTreeTableNode) element).setOutput(value)) {
			parentTestCasePart.getTreeTableInput().setDirty(true);
			treeViewer.refresh(element);
		}
	}
}
