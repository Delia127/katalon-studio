package com.kms.katalon.composer.testcase.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.dialogs.ClosureListInputBuilderDialog;

public class ClosureListInputCellEditor extends AbstractAstDialogCellEditor {
	public ClosureListInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof ClosureListExpression) {
					return null;
				}
				return getValidatorMessage(ClosureListExpression.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new ClosureListInputBuilderDialog(shell, getValue() == null ? null : (ClosureListExpression) getValue(),
				scriptClass);
	}
}