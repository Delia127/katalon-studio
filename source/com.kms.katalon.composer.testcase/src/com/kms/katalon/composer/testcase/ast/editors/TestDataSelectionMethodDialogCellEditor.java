package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.editors.TestDataSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataSelectionMethodDialogCellEditor extends TestDataSelectionDialogCellEditor {
    private MethodCallExpressionWrapper methodCall;

    public TestDataSelectionMethodDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        DataFileEntity testData = null;
        if (value instanceof MethodCallExpressionWrapper
                && AstEntityInputUtil.isTestDataArgument((MethodCallExpressionWrapper) value)) {
            methodCall = (MethodCallExpressionWrapper) value;
            ExpressionWrapper testDataExpression = AstEntityInputUtil.getTestDataObject(methodCall);
            if (testDataExpression == null) {
                return;
            }
            try {
                testData = TestDataController.getInstance().getTestDataByDisplayId(testDataExpression.getText());
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (value instanceof TestDataTreeEntity) {
            try {
                testData = (DataFileEntity) ((TestDataTreeEntity) value).getObject();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        if (testData == null) {
            return;
        }
        super.doSetValue(testData);
    }

    @Override
    protected MethodCallExpressionWrapper doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof DataFileEntity)) {
            return null;
        }
        AstEntityInputUtil.setTestDataObject(methodCall, ((DataFileEntity) value).getIdForDisplay());
        return methodCall;
    }
}
