package com.kms.katalon.composer.testcase.ast.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.editors.TestCaseSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseSelectionMethodCallBuilderDialogCellEditor extends TestCaseSelectionDialogCellEditor {
    private MethodCallExpressionWrapper methodCall;

    public TestCaseSelectionMethodCallBuilderDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        TestCaseEntity testCase = null;
        if (value instanceof MethodCallExpressionWrapper) {
            methodCall = ((MethodCallExpressionWrapper) value).clone();
            String testCaseId = AstEntityInputUtil.findTestCaseIdArgumentFromFindTestCaseMethodCall(methodCall);
            if (testCaseId == null) {
                return;
            }
            try {
                testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (value instanceof TestCaseTreeEntity) {
            try {
                testCase = ((TestCaseTreeEntity) value).getObject();
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
    public ITreeEntity getInitialSelection() {
        if (!(getValue() instanceof MethodCallExpressionWrapper)) {
            return super.getInitialSelection();
        }
        String testCaseId = AstEntityInputUtil.findTestCaseIdArgumentFromFindTestCaseMethodCall(methodCall);
        if (StringUtils.isEmpty(testCaseId)) {
            return super.getInitialSelection();
        }
        try {
            TestCaseEntity selectedTestCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
            return new TestCaseTreeEntity(selectedTestCase, TreeEntityUtil.createSelectedTreeEntityHierachy(
                    selectedTestCase.getParentFolder(), getRootFolder()));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return super.getInitialSelection();
    }

    @Override
    protected MethodCallExpressionWrapper doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof TestCaseEntity)) {
            return null;
        }
        AstEntityInputUtil.setTestCaseIdIntoFindTestCaseMethodCall(methodCall,
                ((TestCaseEntity) value).getIdForDisplay());
        return methodCall;
    }
}
