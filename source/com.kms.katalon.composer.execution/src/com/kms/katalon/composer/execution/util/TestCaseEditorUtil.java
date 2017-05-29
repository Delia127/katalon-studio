package com.kms.katalon.composer.execution.util;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.kms.katalon.composer.execution.trace.LogExceptionNavigator;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;

@SuppressWarnings("restriction")
public class TestCaseEditorUtil {
    public static AbstractTextEditor getTestCaseEditorByScriptName(String fileName) throws Exception {
        TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(fileName);
        return (testCase != null) ? getTestScriptEditor(testCase) : null;
    }

    public static AbstractTextEditor getTestScriptEditor(TestCaseEntity testCase) {
        MPart compatibilityEditorPart = new LogExceptionNavigator().getTestCaseGroovyEditor(testCase);
        CompatibilityEditor compatibilityEditor = (CompatibilityEditor) compatibilityEditorPart.getObject();
        return (AbstractTextEditor) compatibilityEditor.getEditor();
    }
}
