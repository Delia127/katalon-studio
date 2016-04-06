package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ClosureListInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;

public class ClosureListInputCellEditor extends AstDialogCellEditor {
    public ClosureListInputCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, ClosureListExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new ClosureListInputBuilderDialog(shell, (ClosureListExpressionWrapper) getValue());
    }
}