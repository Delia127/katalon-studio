package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.testcase.ast.dialogs.PropertyInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;

public class PropertyInputCellEditor extends AstDialogCellEditor {
    public PropertyInputCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, PropertyExpressionWrapper.class);
    }

    @Override
    protected IAstDialogBuilder getDialog(Shell shell) {
        return new PropertyInputBuilderDialog(shell, (PropertyExpressionWrapper) getValue());
    }
}