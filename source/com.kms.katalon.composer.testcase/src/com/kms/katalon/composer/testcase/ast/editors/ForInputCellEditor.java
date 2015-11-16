package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ForInputBuilderDialog;

public class ForInputCellEditor extends AbstractAstDialogCellEditor {
	public ForInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof ForStatement) {
					return null;
				}
				return getValidatorMessage(ForStatement.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new ForInputBuilderDialog(shell, getValue() == null ? null : (ForStatement) getValue(), scriptClass);
	}
	
	
}