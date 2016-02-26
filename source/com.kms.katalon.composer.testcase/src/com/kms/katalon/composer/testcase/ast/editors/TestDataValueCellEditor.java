package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.TestDataValueBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;

public class TestDataValueCellEditor extends AstDialogCellEditor {
    public TestDataValueCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, MethodCallExpressionWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new TestDataValueBuilderDialog(shell, (MethodCallExpressionWrapper) getValue());
    }
}