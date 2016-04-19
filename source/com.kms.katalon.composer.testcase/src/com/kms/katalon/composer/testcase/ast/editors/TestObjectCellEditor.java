package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.testcase.ast.dialogs.TestObjectBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class TestObjectCellEditor extends AstDialogCellEditor {
    private boolean haveOtherTypes;

    public TestObjectCellEditor(Composite parent, String defaultContent, boolean haveOtherTypes) {
        super(parent, defaultContent, ExpressionWrapper.class);
        this.haveOtherTypes = haveOtherTypes;
    }

    @Override
    protected IAstDialogBuilder getDialog(Shell shell) {
        return new TestObjectBuilderDialog(shell, (ExpressionWrapper) getValue(),
                haveOtherTypes);
    }
}
