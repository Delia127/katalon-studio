package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ListInputBuilderDialog;

public class ListInputCellEditor extends AbstractAstDialogCellEditor {

	public ListInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof ListExpression || value == null) {
					return null;
				}
				return getValidatorMessage(ListExpression.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new ListInputBuilderDialog(shell, (getValue() == null) ? null : (ListExpression) getValue(), scriptClass);
	}

}
