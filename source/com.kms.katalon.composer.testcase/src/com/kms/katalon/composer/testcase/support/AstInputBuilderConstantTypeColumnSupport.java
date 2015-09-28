package com.kms.katalon.composer.testcase.support;

import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.model.ConstantValueType;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;

public class AstInputBuilderConstantTypeColumnSupport extends EditingSupport {
	protected AstBuilderDialog parentDialog;

	public AstInputBuilderConstantTypeColumnSupport(ColumnViewer viewer, AstBuilderDialog parentDialog) {
		super(viewer);
		this.parentDialog = parentDialog;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof ConstantExpression && value instanceof Integer && (int) value != -1
				&& (int) value < ConstantValueType.values().length) {
			ConstantValueType newConstantValueType = ConstantValueType.values()[(int) value];
			ConstantValueType oldConstantValueType = AstTreeTableInputUtil
					.getConstantValueTypeFromConstantExpression((ConstantExpression) element);
			if (newConstantValueType != oldConstantValueType) {
				Expression newExpression = AstTreeTableInputUtil.generateNewConstantExpression(newConstantValueType);
				if (newExpression != null) {
					parentDialog.changeObject(element, newExpression);
					getViewer().refresh();
				}
			}
		}
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof ConstantExpression) {
			return AstTreeTableInputUtil.getConstantValueTypeFromConstantExpression((ConstantExpression) element)
					.ordinal();
		}
		return -1;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new ComboBoxCellEditor((Composite) getViewer().getControl(),
				AstTreeTableInputUtil.getConstantValueTypeStringList());
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof ConstantExpression) {
			return true;
		}
		return false;
	}
}
