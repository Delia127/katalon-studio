package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.CatchInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;

public class CatchCellEditor extends AstDialogCellEditor {
    public CatchCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, CatchStatementWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new CatchInputBuilderDialog(shell, (CatchStatementWrapper) getValue());
    }
}