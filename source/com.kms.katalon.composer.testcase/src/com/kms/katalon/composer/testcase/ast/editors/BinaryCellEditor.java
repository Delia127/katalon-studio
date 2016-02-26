package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.BinaryBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;

public class BinaryCellEditor extends AstDialogCellEditor {
    public BinaryCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, BinaryExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new BinaryBuilderDialog(shell, (BinaryExpressionWrapper) getValue());
    }

}