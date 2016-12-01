package com.kms.katalon.composer.testcase.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.AnnotationNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.core.annotation.SetUp;
import com.kms.katalon.core.annotation.TearDown;
import com.kms.katalon.core.annotation.TearDownIfError;
import com.kms.katalon.core.annotation.TearDownIfFailed;
import com.kms.katalon.core.annotation.TearDownIfPassed;

public class AstTestScriptGeneratorProvider {
    public static String generateScriptForExecuteFromTestStep(ScriptNodeWrapper scriptWrapper,
            StatementWrapper selectedNode) {
        // only allow top nodes for now
        if (!(selectedNode.getParent() instanceof BlockStatementWrapper
                && selectedNode.getParent().getParent() instanceof MethodNodeWrapper)) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    ComposerTestcaseMessageConstants.ERR_MSG_UNABLE_TO_EXECUTE_FROM_TEST_STEP_STEP_INSIDE_BLOCK);
            return null;
        }
        ScriptNodeWrapper clonedScript = createClonedScript(selectedNode, scriptWrapper);
        StringBuilder scriptBuilder = new StringBuilder();
        GroovyWrapperParser parser = new GroovyWrapperParser(scriptBuilder);
        parser.parseGroovyAstIntoScript(clonedScript);
        return scriptBuilder.toString();
    }

    private static ScriptNodeWrapper createClonedScript(StatementWrapper selectedNode,
            ScriptNodeWrapper scriptWrapper) {
        MethodNodeWrapper methodNode = (MethodNodeWrapper) selectedNode.getParent().getParent();
        int methodIndex = scriptWrapper.getMethods().indexOf(methodNode);
        int statementIndex = methodNode.getBlock().getStatements().indexOf(selectedNode);

        ScriptNodeWrapper clonedScript = scriptWrapper.clone();
        List<MethodNodeWrapper> allMethods = clonedScript.getMethods();
        List<MethodNodeWrapper> setupMethods = collectMethod(SetUp.class, clonedScript);
        List<MethodNodeWrapper> mainMethods = new ArrayList<>();
        mainMethods.add(clonedScript.getRunMethod());
        List<MethodNodeWrapper> tearDownIfPassedMethods = collectMethod(TearDownIfPassed.class, clonedScript);
        List<MethodNodeWrapper> tearDownIfFailedMethods = collectMethod(TearDownIfFailed.class, clonedScript);
        List<MethodNodeWrapper> tearDownIfErrorMethods = collectMethod(TearDownIfError.class, clonedScript);
        List<MethodNodeWrapper> tearDownMethods = collectMethod(TearDown.class, clonedScript);

        List<List<MethodNodeWrapper>> methodsCollections = new ArrayList<>();
        methodsCollections.add(setupMethods);
        methodsCollections.add(mainMethods);
        methodsCollections.add(tearDownIfPassedMethods);
        methodsCollections.add(tearDownIfFailedMethods);
        methodsCollections.add(tearDownIfErrorMethods);
        methodsCollections.add(tearDownMethods);
        iterateThroughMethodsCollections(methodIndex, statementIndex, methodsCollections, allMethods);
        return clonedScript;
    }

    private static void iterateThroughMethodsCollections(int seletedMethodIndex, int selectedStatementIndex,
            List<List<MethodNodeWrapper>> methodsColletions, List<MethodNodeWrapper> allMethods) {
        boolean reachSelectedStatement = false;
        for (List<MethodNodeWrapper> methods : methodsColletions) {
            if (reachSelectedStatement) {
                break;
            }
            reachSelectedStatement = iterateThroughMethods(seletedMethodIndex, selectedStatementIndex, methods,
                    allMethods);
        }
    }

    private static boolean iterateThroughMethods(int seletedMethodIndex, int selectedStatementIndex,
            List<MethodNodeWrapper> methods, List<MethodNodeWrapper> allMethods) {
        boolean reachSelectedStatement = false;
        for (MethodNodeWrapper method : methods) {
            if (reachSelectedStatement) {
                break;
            }
            int methodIndex = allMethods.indexOf(method);
            List<StatementWrapper> statements = method.getBlock().getStatements();
            for (int childStatementIndex = 0; childStatementIndex < statements.size(); childStatementIndex++) {
                if (methodIndex == seletedMethodIndex && childStatementIndex == selectedStatementIndex) {
                    reachSelectedStatement = true;
                    break;
                }
                statements.get(childStatementIndex).disable();
            }
        }
        return reachSelectedStatement;
    }

    private static List<MethodNodeWrapper> collectMethod(Class<?> annotationClass, ScriptNodeWrapper clonedScript) {
        List<MethodNodeWrapper> methods = new ArrayList<>();
        for (MethodNodeWrapper method : clonedScript.getMethods()) {
            for (AnnotationNodeWrapper annotationNode : method.getAnnotations()) {
                String annotationNodeName = annotationNode.getClassNode().getName();
                if (annotationNodeName.equals(annotationClass.getName())
                        || annotationNodeName.equals(annotationClass.getSimpleName())) {
                    methods.add(method);
                    break;
                }
            }
        }
        return methods;
    }
}
