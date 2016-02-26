package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ThrowableInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;

public class ThrowableInputCellEditor extends AstDialogCellEditor {
	public ThrowableInputCellEditor(Composite parent, String defaultContent) {
		super(parent, defaultContent, ConstructorCallExpressionWrapper.class);
	}

	@Override
	protected AbstractAstBuilderDialog getDialog(Shell shell) {
		return new ThrowableInputBuilderDialog(shell, (ConstructorCallExpressionWrapper) getValue());
	}

}
