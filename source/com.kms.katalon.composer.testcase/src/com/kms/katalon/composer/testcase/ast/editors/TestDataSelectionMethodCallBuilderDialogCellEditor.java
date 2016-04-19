package com.kms.katalon.composer.testcase.ast.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testcase.editors.TestDataSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataSelectionMethodCallBuilderDialogCellEditor extends TestDataSelectionDialogCellEditor {
    private MethodCallExpressionWrapper methodCall;

    public TestDataSelectionMethodCallBuilderDialogCellEditor(Composite parent, String defaultContent) {
        super(parent, defaultContent);
    }

    @Override
    protected void doSetValue(Object value) {
        DataFileEntity testData = null;
        if (value instanceof MethodCallExpressionWrapper) {
            methodCall = ((MethodCallExpressionWrapper) value).clone();
            String testDataId = AstEntityInputUtil.findTestDataIdFromFindTestDataMethodCall(methodCall);
            if (testDataId == null) {
                return;
            }
            try {
                testData = TestDataController.getInstance().getTestDataByDisplayId(testDataId);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (value instanceof TestDataTreeEntity) {
            try {
                testData = ((TestDataTreeEntity) value).getObject();
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
    public ITreeEntity getInitialSelection() {
        if (!(getValue() instanceof MethodCallExpressionWrapper)) {
            return super.getInitialSelection();
        }
        String testDataId = AstEntityInputUtil.findTestDataIdFromFindTestDataMethodCall(methodCall);
        if (StringUtils.isEmpty(testDataId)) {
            return super.getInitialSelection();
        }
        try {
            DataFileEntity selectedDataFile = TestDataController.getInstance().getTestDataByDisplayId(
                    testDataId);
            return new TestDataTreeEntity(selectedDataFile, TreeEntityUtil.createSelectedTreeEntityHierachy(
                    selectedDataFile.getParentFolder(), getRootFolder()));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return super.getInitialSelection();
    }

    @Override
    protected MethodCallExpressionWrapper doGetValue() {
        Object value = super.doGetValue();
        if (!(value instanceof DataFileEntity)) {
            return null;
        }
        AstEntityInputUtil.setTestDataIdIntoFindTestDataMethodCall(methodCall,
                ((DataFileEntity) value).getIdForDisplay());
        return methodCall;
    }
}
