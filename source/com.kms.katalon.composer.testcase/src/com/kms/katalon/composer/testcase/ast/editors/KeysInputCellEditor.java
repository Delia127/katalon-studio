package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.KeysInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;

public class KeysInputCellEditor extends AstDialogCellEditor {
    public KeysInputCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, MethodCallExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new KeysInputBuilderDialog(shell, (MethodCallExpressionWrapper) getValue());
    }

}
