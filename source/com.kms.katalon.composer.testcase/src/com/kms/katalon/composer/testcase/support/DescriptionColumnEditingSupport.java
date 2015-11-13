package com.kms.katalon.composer.testcase.support;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.TestStepDescriptionBuilderCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.parts.TestCasePart;
import com.kms.katalon.composer.testcase.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class DescriptionColumnEditingSupport extends EditingSupport {
	private TreeViewer treeViewer;
	private TestCasePart parentTestCasePart;

	public DescriptionColumnEditingSupport(TreeViewer viewer, TestCasePart parentTestCasePart) {
		super(viewer);
		this.treeViewer = viewer;
		this.parentTestCasePart = parentTestCasePart;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element instanceof AstStatementTreeTableNode) {
			return new TestStepDescriptionBuilderCellEditor(treeViewer.getTree());
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof AstStatementTreeTableNode) {
			return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof AstStatementTreeTableNode) {
			AstStatementTreeTableNode statementNode = ((AstStatementTreeTableNode) element);
			Object descriptionValue = AstTreeTableValueUtil.getValue(statementNode.getDescription(),
					statementNode.getScriptClass());
			if (descriptionValue instanceof String) {
				return String.valueOf(descriptionValue);
			}
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof AstStatementTreeTableNode && value instanceof String) {
			AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) element;
			try {
				TestCaseTreeTableInput treeTableInput = parentTestCasePart.getTreeTableInput();
				if (!statementNode.hasDescription()) {
					ExpressionStatement commentStatement = new ExpressionStatement(new ConstantExpression(value));
					treeTableInput.addASTObjectBefore(commentStatement, statementNode);
					statementNode.setDescription(commentStatement);
				} else {
					statementNode.getDescription().setExpression(new ConstantExpression(value));
					treeTableInput.setDirty(true);
				}
				treeTableInput.refresh(statementNode.getParent());
			} catch (Exception e) {
				LoggerSingleton.logError(e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
						StringConstants.PA_ERROR_MSG_CANNOT_ADD_DESCRIPTION);
			}
		}
	}
}
