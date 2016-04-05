package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.MethodObjectBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;

public class MethodObjectBuilderCellEditor extends AstDialogCellEditor {
    private ASTNodeWrapper parentNode;
    
	public MethodObjectBuilderCellEditor(Composite parent, String defaultContent, ASTNodeWrapper parentNode) {
		super(parent, defaultContent, MethodNodeWrapper.class);
		this.parentNode = parentNode;
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new MethodObjectBuilderDialog(shell, (MethodNodeWrapper) getValue(), parentNode);
	}

}