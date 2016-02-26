package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ForInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;

public class ForInputCellEditor extends AstDialogCellEditor {
	public ForInputCellEditor(Composite parent, String defaultContent) {
		super(parent, defaultContent, ForStatementWrapper.class);
	}

	@Override
	protected AbstractAstBuilderDialog getDialog(Shell shell) {
		return new ForInputBuilderDialog(shell, (ForStatementWrapper) getValue());
	}
	
	
}