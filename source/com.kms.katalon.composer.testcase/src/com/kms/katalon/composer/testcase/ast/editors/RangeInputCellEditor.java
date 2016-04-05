package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.RangeInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;

public class RangeInputCellEditor extends AstDialogCellEditor {
    public RangeInputCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, RangeExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new RangeInputBuilderDialog(shell, (RangeExpressionWrapper) getValue());
    }
}
