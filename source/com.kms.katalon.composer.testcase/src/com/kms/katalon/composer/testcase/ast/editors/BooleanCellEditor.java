package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.BooleanBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;

public class BooleanCellEditor extends AstDialogCellEditor {
    public BooleanCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, BooleanExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new BooleanBuilderDialog(shell, (BooleanExpressionWrapper) getValue());
    }
}