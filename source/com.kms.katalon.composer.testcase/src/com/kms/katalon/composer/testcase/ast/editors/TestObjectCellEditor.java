package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.testcase.ast.dialogs.TestObjectBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;

public class TestObjectCellEditor extends AstDialogCellEditor {
    private boolean haveOtherTypes;

    private ITestCasePart testCasePart;

    public TestObjectCellEditor(Composite parent, String defaultContent, boolean haveOtherTypes) {
        super(parent, defaultContent, ExpressionWrapper.class);
        this.haveOtherTypes = haveOtherTypes;
    }

    @Override
    protected IAstDialogBuilder getDialog(Shell shell) {
        TestObjectBuilderDialog astDialog = new TestObjectBuilderDialog(shell, (ExpressionWrapper) getValue(), haveOtherTypes);
        astDialog.setTestCasePart(getTestCasePart());
        return astDialog;
    }
    
    public void setTestCasePart(ITestCasePart testCasePart) {
        this.testCasePart = testCasePart;
    }
    
    public ITestCasePart getTestCasePart() {
        return testCasePart;
    }
}
