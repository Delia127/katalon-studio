package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.MapInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;

public class MapInputCellEditor extends AstDialogCellEditor {
	public MapInputCellEditor(Composite parent, String defaultContent) {
		super(parent, defaultContent, MapExpressionWrapper.class);
	}

	@Override
	protected AbstractAstBuilderDialog getDialog(Shell shell) {
		return new MapInputBuilderDialog(shell, (MapExpressionWrapper) getValue());
	}

}
