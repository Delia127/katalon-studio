package com.kms.katalon.composer.testcase.support;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.treetable.AstTreeTableNode;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestObjectEditingSupport extends EditingSupport {
	private TreeViewer treeViewer;
	private TestCasePart parentTestCasePart;

	public TestObjectEditingSupport(TreeViewer treeViewer, TestCasePart parentTestCasePart) {
		super(treeViewer);
		this.treeViewer = treeViewer;
		this.parentTestCasePart = parentTestCasePart;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getCellEditorForTestObject(treeViewer.getTree());
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).isTestObjectEditable();
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getTestObject();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof AstTreeTableNode && ((AstTreeTableNode) element).setTestObject(value)) {
			if (element instanceof AstCallTestCaseKeywordTreeTableNode) {
				List<VariableEntity> testCaseVariables = ((AstCallTestCaseKeywordTreeTableNode) element)
						.getCallTestCaseVariables();
				parentTestCasePart
						.addVariables(testCaseVariables.toArray(new VariableEntity[testCaseVariables.size()]));
			}
			parentTestCasePart.getTreeTableInput().setDirty(true);
			treeViewer.refresh(element);
		}
	}
}
