package com.kms.katalon.composer.execution.util;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.kms.katalon.composer.execution.exceptions.StepNotFoundException;
import com.kms.katalon.composer.execution.trace.LogExceptionNavigator;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
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

    public static void navigateToTestStep(TestCaseEntity testCase, int stepIndex) throws Exception {
        TestCaseCompositePart testCaseComposite = new LogExceptionNavigator().openTestCaseComposite(testCase);
        testCaseComposite.setScriptContentToManual();
        List<? extends ASTNodeWrapper> classAstNodes = testCaseComposite.getChildTestCasePart()
                .getTreeTableInput()
                .getMainClassNode()
                .getRunMethod()
                .getAstChildren();
        if (classAstNodes == null || classAstNodes.isEmpty()) {
            throw new StepNotFoundException(stepIndex, testCase.getIdForDisplay());
        }

        ASTNodeWrapper blockStatement = classAstNodes.get(0);
        if (!(blockStatement instanceof BlockStatementWrapper)) {
            throw new StepNotFoundException(stepIndex, testCase.getIdForDisplay());
        }
        List<? extends ASTNodeWrapper> astNodes = blockStatement.getAstChildren();
        if (astNodes.size() <= stepIndex - 1) {
            throw new StepNotFoundException(stepIndex, testCase.getIdForDisplay());
        }

        testCaseComposite.setSelectedPart(testCaseComposite.getChildCompatibilityPart());

        int lineNumber = astNodes.get(stepIndex - 1).getLineNumber();
        if (lineNumber < 0) {
            throw new StepNotFoundException(stepIndex, testCase.getIdForDisplay());
        }
        CompatibilityEditor groovyEditor = (CompatibilityEditor) testCaseComposite.getChildCompatibilityPart()
                .getObject();
        AbstractTextEditor editor = (AbstractTextEditor) groovyEditor.getEditor();
        IDocument document = editor.getDocumentProvider().getDocument(groovyEditor.getEditor().getEditorInput());
        editor.selectAndReveal(document.getLineOffset(lineNumber - 1), document.getLineLength(lineNumber - 1));
    }
}
