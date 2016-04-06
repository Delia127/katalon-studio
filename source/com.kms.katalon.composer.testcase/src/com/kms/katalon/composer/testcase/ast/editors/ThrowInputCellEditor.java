package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ThrowBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;

public class ThrowInputCellEditor extends AstDialogCellEditor {
	public ThrowInputCellEditor(Composite parent, String defaultContent) {
		super(parent, defaultContent, ThrowStatementWrapper.class);
	}

	@Override
	protected AbstractAstBuilderDialog getDialog(Shell shell) {
		return new ThrowBuilderDialog(shell, (ThrowStatementWrapper) getValue());
	}

}
