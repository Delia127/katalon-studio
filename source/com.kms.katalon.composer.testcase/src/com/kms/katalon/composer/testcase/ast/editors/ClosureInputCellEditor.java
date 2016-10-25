package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.ClosureBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureExpressionWrapper;

public class ClosureInputCellEditor extends AstDialogCellEditor {

    private ASTNodeWrapper parentNode;
    
    public ClosureInputCellEditor(Composite parent, String defaultContent, ASTNodeWrapper parentNode) {
        super(parent, defaultContent, ClosureExpressionWrapper.class);
        this.parentNode = parentNode;
    }

    @Override
    protected IAstDialogBuilder getDialog(Shell shell) {
        return new ClosureBuilderDialog(shell, (ClosureExpressionWrapper) getValue(), parentNode);
    }

}
