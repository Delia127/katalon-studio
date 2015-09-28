package com.kms.katalon.composer.testcase.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.dialogs.BooleanBuilderDialog;

public class BooleanCellEditor extends AbstractAstDialogCellEditor {
	public BooleanCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof BooleanExpression) {
					return null;
				}
				return getValidatorMessage(BooleanExpression.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new BooleanBuilderDialog(shell, getValue() == null ? null : (BooleanExpression) getValue(), scriptClass);
	}
}