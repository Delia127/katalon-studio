package com.kms.katalon.composer.testcase.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.dialogs.TestDataValueBuilderDialog;

public class TestDataValueCellEditor extends AbstractAstDialogCellEditor {
	public TestDataValueCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof MethodCallExpression) {
					return null;
				}
				return getValidatorMessage(MethodCallExpression.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new TestDataValueBuilderDialog(shell, getValue()  == null ? null : (MethodCallExpression) getValue(), scriptClass);
	}

}