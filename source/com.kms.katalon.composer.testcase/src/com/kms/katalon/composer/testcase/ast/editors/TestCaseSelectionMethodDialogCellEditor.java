package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.editors.TestCaseSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseSelectionMethodDialogCellEditor extends TestCaseSelectionDialogCellEditor {
    private MethodCallExpressionWrapper methodCall;

    public TestCaseSelectionMethodDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        TestCaseEntity testCase = null;
        if (value instanceof MethodCallExpressionWrapper
                && AstEntityInputUtil.isCallTestCaseArgument((MethodCallExpressionWrapper) value)) {
            methodCall = (MethodCallExpressionWrapper) value;
            ExpressionWrapper testCaseExpression = AstEntityInputUtil.getCallTestCaseParam(methodCall);
            if (testCaseExpression == null) {
                return;
            }
            try {
                testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseExpression.getText());
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (value instanceof TestCaseTreeEntity) {
            try {
                testCase = (TestCaseEntity) ((TestCaseTreeEntity) value).getObject();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        if (testCase == null) {
            return;
        }
        super.doSetValue(testCase);
    }

    @Override
    protected MethodCallExpressionWrapper doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof TestCaseEntity)) {
            return null;
        }
        AstEntityInputUtil.setCallTestCaseParam(methodCall, ((TestCaseEntity) value).getIdForDisplay());
        return methodCall;
    }
}
