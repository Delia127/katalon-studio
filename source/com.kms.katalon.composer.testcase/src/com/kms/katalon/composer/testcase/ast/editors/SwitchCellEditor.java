package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.SwitchInputBuilderDialog;

public class SwitchCellEditor extends AbstractAstDialogCellEditor {
	public SwitchCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent, scriptClass);
		this.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (value instanceof SwitchStatement) {
					return null;
				}
				return getValidatorMessage(SwitchStatement.class.getName());
			}
		});
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new SwitchInputBuilderDialog(shell, getValue() == null ? null : (SwitchStatement) getValue(), scriptClass);
	}
}