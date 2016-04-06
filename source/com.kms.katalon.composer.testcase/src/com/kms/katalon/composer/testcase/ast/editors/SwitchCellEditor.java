package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.SwitchInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;

public class SwitchCellEditor extends AstDialogCellEditor {
	public SwitchCellEditor(Composite parent, String defaultContent) {
		super(parent, defaultContent, SwitchStatementWrapper.class);
	}

	@Override
	protected AbstractAstBuilderDialog getDialog(Shell shell) {
		return new SwitchInputBuilderDialog(shell, (SwitchStatementWrapper) getValue());
	}
}