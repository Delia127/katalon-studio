package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ListInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;

public class ListInputCellEditor extends AstDialogCellEditor {
    public ListInputCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, ListExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new ListInputBuilderDialog(shell, (ListExpressionWrapper) getValue());
    }

}
