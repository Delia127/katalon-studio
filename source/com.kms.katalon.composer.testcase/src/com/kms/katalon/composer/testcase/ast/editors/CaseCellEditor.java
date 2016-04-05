package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.CaseInputBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;

public class CaseCellEditor extends AstDialogCellEditor {
    public CaseCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent, CaseStatementWrapper.class);
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new CaseInputBuilderDialog(shell, (CaseStatementWrapper) getValue());
    }
}